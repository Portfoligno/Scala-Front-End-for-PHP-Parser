package io.github.portfoligno.php.parser

import cats.effect.{ContextShift, IO}
import cats.effect.internals.IOContextShift

import scala.concurrent.ExecutionContext

package object test {
  private[test] lazy val log: org.log4s.Logger = org.log4s.getLogger

  private[test]
  implicit def contextSwitch: ContextShift[IO] = IOContextShift.global

  private[test]
  implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global


  private[test]
  val testString =
    """<?php
      |
      |function test($foo)
      |{
      |    var_dump($foo);
      |}
    """.stripMargin
}
