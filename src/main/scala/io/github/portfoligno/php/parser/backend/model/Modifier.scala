package io.github.portfoligno.php.parser.backend.model

sealed trait Modifier

object Modifier extends BitsConverter[Modifier] {
  val MODIFIER_PUBLIC: _0.type = _0
  val MODIFIER_PROTECTED: _1.type = _1
  val MODIFIER_PRIVATE: _2.type = _2
  val MODIFIER_STATIC: _3.type = _3
  val MODIFIER_ABSTRACT: _4.type = _4
  val MODIFIER_FINAL: _5.type = _5

  case object _0 extends Modifier
  case object _1 extends Modifier
  case object _2 extends Modifier
  case object _3 extends Modifier
  case object _4 extends Modifier
  case object _5 extends Modifier
}
