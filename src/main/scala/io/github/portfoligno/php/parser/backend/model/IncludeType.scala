package io.github.portfoligno.php.parser.backend.model

sealed trait IncludeType

object IncludeType extends OrdinalConverter[IncludeType] {
  val TYPE_INCLUDE: _1.type = _1
  val TYPE_INCLUDE_ONCE: _2.type = _2
  val TYPE_REQUIRE: _3.type = _3
  val TYPE_REQUIRE_ONCE: _4.type = _4

  case object _1 extends IncludeType
  case object _2 extends IncludeType
  case object _3 extends IncludeType
  case object _4 extends IncludeType
}
