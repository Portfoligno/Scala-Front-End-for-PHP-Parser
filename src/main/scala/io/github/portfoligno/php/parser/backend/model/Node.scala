package io.github.portfoligno.php.parser.backend.model

import io.circe.generic.extras.ConfiguredJsonCodec

import scala.collection.immutable.Seq

@ConfiguredJsonCodec
sealed trait Node

object Node extends NodeInstances {
  sealed trait FunctionLike extends Node {
    val byRef: Boolean
    val params: Seq[Param]
    val returnType: Option[`NullableType|IdentifierBase|Name`]
    val stmts: Option[Seq[Stmt]]
  }

  // Marker traits to represent union types
  @ConfiguredJsonCodec
  sealed trait `NullableType|IdentifierBase|Name` extends Node
  @ConfiguredJsonCodec
  sealed trait `IdentifierBase|Name` extends `NullableType|IdentifierBase|Name`

  @ConfiguredJsonCodec
  sealed trait `Class|Name|Expr` extends Node
  @ConfiguredJsonCodec
  sealed trait `Name|Expr` extends `Class|Name|Expr`

  @ConfiguredJsonCodec
  sealed trait `IdentifierBase|Error` extends Node

  @ConfiguredJsonCodec
  sealed trait `IdentifierBase|Expr` extends Node
  @ConfiguredJsonCodec
  sealed trait `VarLikeIdentifier|Expr` extends `IdentifierBase|Expr`


  @ConfiguredJsonCodec
  final case class Arg(value: Expr, byRef: Boolean, unpack: Boolean) extends Node
  @ConfiguredJsonCodec
  final case class Const(name: IdentifierBase, value: Expr) extends Node
  final case class NullableType(`type`: `IdentifierBase|Name`) extends `NullableType|IdentifierBase|Name`
  @ConfiguredJsonCodec
  final case class Param(
    `type`: Option[`NullableType|IdentifierBase|Name`],
    byRef: Boolean,
    variadic: Boolean,
    `var`: Expr.`Variable|Error`,
    default: Option[Expr]
  )
    extends Node

  // Avoid making VarLikeIdentifier extends Identifier
  @ConfiguredJsonCodec
  sealed trait IdentifierBase extends `IdentifierBase|Name` with `IdentifierBase|Error` with `IdentifierBase|Expr` {
    val name: String
  }
  final case class Identifier(override val name: String) extends IdentifierBase
  @ConfiguredJsonCodec
  final case class VarLikeIdentifier(override val name: String) extends IdentifierBase with `VarLikeIdentifier|Expr`


  @ConfiguredJsonCodec
  sealed trait Expr extends `Name|Expr` with `VarLikeIdentifier|Expr`

  object Expr {
    // Marker traits to represent union types
    @ConfiguredJsonCodec
    sealed trait `Variable|Error` extends Expr


    final case class ArrayDimFetch(`var`: Expr, dim: Option[Expr]) extends Expr
    @ConfiguredJsonCodec
    final case class ArrayItem(key: Option[Expr], value: Expr, byRef: Boolean) extends Expr
    final case class Array(items: Seq[ArrayItem]) extends Expr
    final case class Assign(`var`: Expr, expr: Expr) extends Expr
    final case class AssignRef(`var`: Expr, expr: Expr) extends Expr
    final case class BitwiseNot(expr: Expr) extends Expr
    final case class BooleanNot(expr: Expr) extends Expr
    final case class ClassConstFetch(`class`: `Name|Expr`, name: `IdentifierBase|Error`) extends Expr
    final case class Clone(expr: Expr) extends Expr
    final case class Closure(
      override val byRef: Boolean,
      override val params: Seq[Param],
      override val returnType: Option[`NullableType|IdentifierBase|Name`],
      override val stmts: Some[Seq[Stmt]],
      static: Boolean,
      uses: Seq[ClosureUse]
    )
      extends Expr with FunctionLike
    @ConfiguredJsonCodec
    final case class ClosureUse(`var`: Variable, byRef: Boolean) extends Expr
    final case class ConstFetch(name: Name) extends Expr
    final case class Empty(expr: Expr) extends Expr
    case object Error extends `Variable|Error` with `IdentifierBase|Error`
    final case class ErrorSuppress(expr: Expr) extends Expr
    final case class Eval(expr: Expr) extends Expr
    final case class Exit(expr: Option[Expr]) extends Expr
    final case class FuncCall(name: `Name|Expr`, args: Seq[Arg]) extends Expr
    final case class Include(expr: Expr, `type`: IncludeType) extends Expr
    final case class Instanceof(expr: Expr, `class`: `Name|Expr`) extends Expr
    final case class Isset(vars: Seq[Expr]) extends Expr
    final case class List(items: Seq[Option[ArrayItem]]) extends Expr
    final case class MethodCall(`var`: Expr, name: `IdentifierBase|Expr`, args: Seq[Arg]) extends Expr
    final case class New(`class`: `Class|Name|Expr`, args: Seq[Arg]) extends Expr
    final case class PostDec(`var`: Expr) extends Expr
    final case class PostInc(`var`: Expr) extends Expr
    final case class PreDec(`var`: Expr) extends Expr
    final case class PreInc(`var`: Expr) extends Expr
    final case class Print(expr: Expr) extends Expr
    final case class PropertyFetch(`var`: Expr, name: `IdentifierBase|Expr`) extends Expr
    final case class ShellExec(parts: Seq[Scalar.EncapsedStringPart]) extends Expr
    final case class StaticCall(`class`: `Name|Expr`, name: `IdentifierBase|Expr`, args: Seq[Arg]) extends Expr
    final case class StaticPropertyFetch(`class`: `Name|Expr`, name: `VarLikeIdentifier|Expr`) extends Expr
    final case class Ternary(cond: Expr, `if`: Option[Expr], `else`: Expr) extends Expr
    final case class UnaryMinus(`var`: Expr) extends Expr
    @ConfiguredJsonCodec
    final case class Variable(name: Either[String, Expr]) extends `Variable|Error`
    final case class YieldFrom(expr: Expr) extends Expr
    final case class Yield(key: Option[Expr], value: Option[Expr]) extends Expr


    sealed trait AssignOp extends Expr {
      val `var`: Expr
      val expr: Expr
    }
    object AssignOp {
      final case class BitwiseAnd(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class BitwiseOr(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class BitwiseXor(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class Concat(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class Div(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class Minus(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class Mod(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class Mul(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class Plus(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class Pow(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class ShiftLeft(override val `var`: Expr, override val expr: Expr) extends AssignOp
      final case class ShiftRight(override val `var`: Expr, override val expr: Expr) extends AssignOp
    }


    sealed trait BinaryOp extends Expr {
      val left: Expr
      val right: Expr
    }
    object BinaryOp {
      final case class BitwiseAnd(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class BitwiseOr(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class BitwiseXor(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class BooleanAnd(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class BooleanOr(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Coalesce(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Concat(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Div(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Equal(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Greater(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class GreaterOrEqual(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Identical(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class LogicalAnd(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class LogicalOr(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class LogicalXor(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Minus(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Mod(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Mul(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class NotEqual(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class NotIdentical(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Plus(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Pow(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class ShiftLeft(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class ShiftRight(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Smaller(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class SmallerOrEqual(override val left: Expr, override val right: Expr) extends BinaryOp
      final case class Spaceship(override val left: Expr, override val right: Expr) extends BinaryOp
    }


    sealed trait Cast extends Expr {
      val expr: Expr
    }
    object Cast {
      final case class Array(override val expr: Expr) extends Cast
      final case class Bool(override val expr: Expr) extends Cast
      final case class Double(override val expr: Expr) extends Cast
      final case class Int(override val expr: Expr) extends Cast
      final case class Object(override val expr: Expr) extends Cast
      final case class String(override val expr: Expr) extends Cast
      final case class Unset(override val expr: Expr) extends Cast
    }
  }


  @ConfiguredJsonCodec
  sealed trait Name extends `IdentifierBase|Name` with `Name|Expr` {
    val parts: Seq[String]
  }
  object Name {
    final case class FullyQualified(override val parts: Seq[String]) extends Name
    final case class Relative(override val parts: Seq[String]) extends Name
  }


  sealed trait Scalar extends Expr

  object Scalar {
    import java.lang.{String => JString}

    final case class DNumber(value: Double) extends Scalar
    final case class Encapsed(parts: Seq[Expr]) extends Scalar
    @ConfiguredJsonCodec
    final case class EncapsedStringPart(value: JString) extends Scalar
    final case class LNumber(value: Int) extends Scalar
    final case class String(value: JString) extends Scalar


    sealed trait MagicConst extends Scalar

    object MagicConst {
      case object Class extends MagicConst
      case object Dir extends MagicConst
      case object File extends MagicConst
      case object Function extends MagicConst
      case object Line extends MagicConst
      case object Method extends MagicConst
      case object Namespace extends MagicConst
      case object Trait extends MagicConst
    }
  }


  @ConfiguredJsonCodec
  sealed trait Stmt extends Node

  object Stmt {
    sealed trait ClassLike extends Stmt {
      val name: Option[IdentifierBase]
      val stmts: Seq[Stmt]
    }

    final case class Break(num: Option[Expr]) extends Stmt
    @ConfiguredJsonCodec
    final case class Case(cond: Option[Expr], stmts: Seq[Stmt]) extends Stmt
    @ConfiguredJsonCodec
    final case class Catch(types: Seq[Name], `var`: Expr.Variable, `stmts`: Seq[Stmt]) extends Stmt
    final case class ClassConst(flags: Modifiers, consts: Seq[Node.Const]) extends Stmt
    final case class ClassMethod(
      override val byRef: Boolean,
      override val params: Seq[Param],
      override val returnType: Option[`NullableType|IdentifierBase|Name`],
      override val stmts: Option[Seq[Stmt]],
      flags: Modifiers,
      name: IdentifierBase
    )
      extends Stmt with FunctionLike
    final case class Class(
      override val name: Option[IdentifierBase],
      override val stmts: Seq[Stmt],
      flags: Modifiers,
      `extends`: Option[Name],
      implements: Seq[Name]
    )
      extends ClassLike with `Class|Name|Expr`
    final case class Const(consts: Seq[Node.Const]) extends Stmt
    final case class Continue(num: Option[Expr]) extends Stmt
    @ConfiguredJsonCodec
    final case class DeclareDeclare(key: IdentifierBase, value: Expr) extends Stmt
    final case class Declare(declares: Seq[DeclareDeclare], stmts: Option[Seq[Stmt]]) extends Stmt
    final case class Do(stmts: Seq[Stmt], cond: Expr) extends Stmt
    final case class Echo(exprs: Seq[Expr]) extends Stmt
    @ConfiguredJsonCodec
    final case class ElseIf(cond: Expr, stmts: Seq[Stmt]) extends Stmt
    @ConfiguredJsonCodec
    final case class Else(stmts: Seq[Stmt]) extends Stmt
    final case class Expression(expr: Expr) extends Stmt
    @ConfiguredJsonCodec
    final case class Finally(stmts: Seq[Stmt]) extends Stmt
    final case class For(init: Seq[Expr], cond: Seq[Expr], loop: Seq[Expr], stmts: Seq[Stmt]) extends Stmt
    final case class Foreach(
      expr: Expr,
      keyVar: Option[Expr],
      byRef: Boolean,
      valueVar: Expr,
      stmts: Seq[Stmt]
    )
      extends Stmt
    final case class Function(
      override val byRef: Boolean,
      override val params: Seq[Param],
      override val returnType: Option[`NullableType|IdentifierBase|Name`],
      override val stmts: Some[Seq[Stmt]],
      name: IdentifierBase
    )
      extends Stmt with FunctionLike
    final case class Global(vars: Seq[Expr]) extends Stmt
    final case class Goto(name: IdentifierBase) extends Stmt
    final case class GroupUse(`type`: Modifiers, prefix: Name, uses: Seq[UseUse]) extends Stmt
    final case class HaltCompiler(remaining: String) extends Stmt
    final case class If(cond: Expr, stmts: Seq[Stmt], elseifs: Seq[ElseIf], `else`: Option[Else]) extends Stmt
    final case class InlineHTML(value: String) extends Stmt
    final case class Interface(`extends`: Seq[Name]) extends Stmt
    final case class Label(name: IdentifierBase) extends Stmt
    final case class Namespace(name: Option[Name], stmts: Seq[Stmt]) extends Stmt
    case object Nop extends Stmt
    final case class Property(flags: Modifiers, props: Seq[PropertyProperty]) extends Stmt
    @ConfiguredJsonCodec
    final case class PropertyProperty(name: VarLikeIdentifier, default: Option[Expr]) extends Stmt
    final case class Return(expr: Option[Expr]) extends Stmt
    @ConfiguredJsonCodec
    final case class StaticVar(`var`: Expr.Variable, default: Option[Expr]) extends Stmt
    final case class Static(vars: Seq[StaticVar]) extends Stmt
    final case class Switch(cond: Expr, cases: Seq[Case]) extends Stmt
    final case class Throw(expr: Expr) extends Stmt
    final case class TraitUse(traits: Seq[Name], adaptations: Seq[TraitUseAdaptation]) extends Stmt
    final case class Trait(override val name: Option[IdentifierBase], override val stmts: Seq[Stmt]) extends ClassLike
    final case class TryCatch(stmts: Seq[Stmt], catches: Seq[Catch], `finally`: Option[Finally]) extends Stmt
    final case class Unset(vars: Seq[Expr]) extends Stmt
    @ConfiguredJsonCodec
    final case class UseUse(`type`: UseType, name: Name, alias: Option[IdentifierBase]) extends Stmt
    final case class Use(`type`: UseType, uses: Seq[UseUse]) extends Stmt
    final case class While(cond: Expr, stmts: Seq[Stmt]) extends Stmt


    @ConfiguredJsonCodec
    sealed trait TraitUseAdaptation extends Stmt {
      val `trait`: Option[Name]
      val method: IdentifierBase
    }
    object TraitUseAdaptation {
      final case class Alias(
        override val `trait`: Option[Name],
        override val method: IdentifierBase,
        newModifier: Option[Modifiers],
        newName: Option[IdentifierBase]
      )
        extends TraitUseAdaptation
      final case class Precedence(
        override val `trait`: Option[Name],
        override val method: IdentifierBase,
        insteadof: Seq[Name]
      )
        extends TraitUseAdaptation
    }
  }
}
