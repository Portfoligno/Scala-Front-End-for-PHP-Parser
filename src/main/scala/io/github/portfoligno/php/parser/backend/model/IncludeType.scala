package io.github.portfoligno.php.parser.backend.model

sealed abstract case class IncludeType(ordinal: Int)

object IncludeType {
  object TYPE_INCLUDE extends IncludeType(1)
  object TYPE_INCLUDE_ONCE extends IncludeType(2)
  object TYPE_REQUIRE extends IncludeType(3)
  object TYPE_REQUIRE_ONCE extends IncludeType(4)
}
