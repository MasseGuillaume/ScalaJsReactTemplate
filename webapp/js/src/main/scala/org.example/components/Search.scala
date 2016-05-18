package org.example
package components

import scala.language.postfixOps

import autowire._
import rpc._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

import japgolly.scalajs.react._, vdom.all._
import japgolly.scalajs.react.extra.router._

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object Search {
  private[Search] case class SearchState(filter: String, stuffs: List[Stuff])

  object Style extends StyleSheet.Inline {
    import dsl._

    val searchInput =
      style(
        border.none,
        height(2 em),
        fontSize(1.5 em),
        padding.`0`,
        width(100 %%),
        &.focus(
          border.none,
          outline.none
        ),
        backgroundColor.transparent
      )

    val stuffList =
      style(
        paddingLeft.`0`
      )

    val stuffElem =
      style(
        display.block
      )

    val stuffLink =
      style(
        color.white,
        textDecoration := "none"
      )
  }

  private val StuffSearch = ReactComponentB[(String, Backend)]("StuffSearch")
    .render_P { case (s, b) =>
      input.text(
        Style.searchInput,
        placeholder := "Search Stuff",
        value       := s,
        onChange   ==> b.onTextChange
      )
    }
    .build

  private def target(stuff: Stuff) =
    StuffPage(stuff.a, stuff.b)

  private val StuffList = ReactComponentB[(List[Stuff], RouterCtl[Page])]("StuffList")
    .render_P{ case (stuffs, ctl) =>
      ul(Style.stuffList)(stuffs.map( stuff =>
        li(Style.stuffElem)(
          a(Style.stuffLink, 
            href := ctl.urlFor(target(stuff)).value,
            ctl.setOnLinkClick(target(stuff)))(
            s"${stuff.a} ${stuff.b}"
          )
        )
      ))
    }.build

  private[Search] class Backend($: BackendScope[Unit, (SearchState, RouterCtl[Page])]) {
    def onTextChange(e: ReactEventI) = {
      e.extract(_.target.value)(value =>
        Callback.future {
          AutowireClient[Api].find(value).call().map( stuffs => 
            $.modState{ case (_, ctl) => (SearchState(value, stuffs), ctl)}
          )
        }
      )
    }

    def render(state: (SearchState, RouterCtl[Page])) = {
      val (SearchState(filter, stuffs), ctl) = state
      div(
        StuffSearch((filter, this)),
        StuffList((stuffs, ctl))
      ) 
    }
  }

  def component(ctl: RouterCtl[Page]) = 
    ReactComponentB[Unit]("StuffSearchApp")
      .initialState((SearchState("", Nil), ctl))
      .renderBackend[Backend]
      .build

  def apply(ctl: RouterCtl[Page]) = {
    val a = component(ctl)
    a()
  }

}