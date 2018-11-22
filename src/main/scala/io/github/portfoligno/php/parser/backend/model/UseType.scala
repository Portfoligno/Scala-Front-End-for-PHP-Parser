package io.github.portfoligno.php.parser.backend.model

sealed trait UseType

object UseType extends OrdinalConverter[UseType] {
  val TYPE_UNKNOWN: _0.type = _0
  val TYPE_NORMAL: _1.type = _1
  val TYPE_FUNCTION: _2.type = _2
  val TYPE_CONSTANT: _3.type = _3

  case object _0 extends UseType
  case object _1 extends UseType
  case object _2 extends UseType
  case object _3 extends UseType
}
