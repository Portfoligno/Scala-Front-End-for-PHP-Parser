package io.github.portfoligno.php.parser.backend.model

sealed abstract case class Modifier(bitMask: Int)

object Modifier {
  object MODIFIER_PUBLIC extends Modifier(1)
  object MODIFIER_PROTECTED extends Modifier(2)
  object MODIFIER_PRIVATE extends Modifier(4)
  object MODIFIER_STATIC extends Modifier(8)
  object MODIFIER_ABSTRACT extends Modifier(16)
  object MODIFIER_FINAL extends Modifier(32)
}
