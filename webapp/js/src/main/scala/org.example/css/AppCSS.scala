package org.example
package css

import components._

import scalacss.ScalaCssReact._
import scalacss.mutable.GlobalRegistry
import scalacss.Defaults._


object AppCSS {

  def load() = {
    GlobalRegistry.register(
      GlobalStyle,
      Header.Style,
      HomeView.Style,
      StuffView.Style,
      Search.Style,
      User.Style
    )
    GlobalRegistry.onRegistration(_.addToDocument())
  }
}
