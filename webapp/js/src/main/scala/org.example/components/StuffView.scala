package org.example
package components

import autowire._
import rpc._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

import japgolly.scalajs.react._, vdom.all._

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object StuffView {
  object Style extends StyleSheet.Inline {
    import dsl._

    val container = style(
      marginLeft(10.px),
      marginRight(10.px)
    )

    val stuff = style(
      width(70.%%),
      display.inlineBlock
    )

    val side = style(
      width(30.%%),
      display.inlineBlock,
      overflow.hidden,
      verticalAlign.top
    )
  }

  private val StuffSearch = ReactComponentB[(String, Backend)]("StuffSearch")
  private val StuffSideBar = ReactComponentB[Stuff]("StuffSideBar")
    .render_P ( stuff =>
      div(stuff.z.toString())
    )
    .build

  private class Backend($: BackendScope[Unit, Option[Stuff]]) {
    def render(a: Option[Stuff]) = {
      a match {
        case Some(stuff) => 
          div(Style.container)(
            div(Style.stuff)("stuff"),
            div(Style.side)(StuffSideBar(stuff))
          )
        case None => div("not found")
      }
    }
  }

  private def View(page: StuffPage) = 
    ReactComponentB[Unit]("Stuff View")
    .initialState(None: Option[Stuff])
    .renderBackend[Backend]
    .componentDidMount(scope =>
      Callback.future {
        val StuffPage(a, b) = page
        AutowireClient[Api].stuffPage(a, b).call().map( r => 
          scope.modState{ case _ => r}
        )
      }
    )
    .build

  val component = ReactComponentB[StuffPage]("Stuff Page View")
    .render_P( stuffPage =>
      div(View(stuffPage)())
    )
    .build
}