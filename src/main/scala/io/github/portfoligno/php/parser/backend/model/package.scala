package io.github.portfoligno.php.parser.backend

import scala.util.matching.Regex

package object model {
  type Modifiers = Set[Modifier]

  private[model]
  implicit class CharSequenceOps(val string: CharSequence) extends AnyVal {
    def replaceFirst(regex: Regex, replacement: String): String =
      regex.replaceFirstIn(string, replacement)
  }
}
