package io.github.portfoligno.php.parser.backend.model

sealed abstract case class UseType(ordinal: Int)

object UseType {
  object TYPE_UNKNOWN extends UseType(0)
  object TYPE_NORMAL extends UseType(1)
  object TYPE_FUNCTION extends UseType(2)
  object TYPE_CONSTANT extends UseType(3)
}
