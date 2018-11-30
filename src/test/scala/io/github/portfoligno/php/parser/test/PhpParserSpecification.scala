package io.github.portfoligno.php.parser.test

import cats.effect.{ContextShift, IO, Resource}
import io.github.portfoligno.php.parser.backend.adapter.{ParserFactoryMode, PhpParser}
import org.scalacheck.Prop._
import org.scalacheck.Properties

object PhpParserSpecification extends Properties(classOf[PhpParser[IO]].getSimpleName) {
  private
  val parser = PhpParser.resource[IO]

  property("parse") = delay {
    val s = parser
      .use(_
        .parse(ParserFactoryMode.PREFER_PHP7, testString)
        .compile
        .toList)
      .map(_.toString)
      .unsafeRunSync()
    log.info(s)

    s != null
  }
}
