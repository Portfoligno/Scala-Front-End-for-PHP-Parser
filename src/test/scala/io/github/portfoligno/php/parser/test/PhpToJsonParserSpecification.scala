package io.github.portfoligno.php.parser.test

import cats.effect.{ContextShift, IO, Resource}
import cats.instances.string._
import fs2.text.utf8Decode
import io.github.portfoligno.php.parser.backend.adapter.{ParserFactoryMode, PhpToJsonParser}
import org.scalacheck.Prop._
import org.scalacheck.Properties

object PhpToJsonParserSpecification extends Properties(classOf[PhpToJsonParser[IO]].getSimpleName) {
  private
  val parser = PhpToJsonParser.resource[IO]

  property("parse") = delay {
    val s = parser
      .use(_
        .parse(ParserFactoryMode.PREFER_PHP7, testString)
        .through(utf8Decode)
        .compile
        .foldMonoid)
      .unsafeRunSync()
    log.info(s)

    s != null
  }
}
