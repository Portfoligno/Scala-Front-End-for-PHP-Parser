package io.github.portfoligno.php.parser.backend.adapter

import cats.effect.Concurrent.memoize
import cats.effect.Resource.liftF
import cats.effect.{ConcurrentEffect, Resource}
import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{EntityBody, Uri}

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait PhpToJsonParser[F[_]] {
  def parse(mode: ParserFactoryMode, source: String): F[EntityBody[F]]
}

object PhpToJsonParser {
  private
  def memoizeApi[F[_] : ConcurrentEffect]: F[F[Uri]] =
    memoize(env("PHP_PARSER_API_ENDPOINT").map {
      case Left(failure) =>
        import org.http4s.Http4s._
        log.warn(s"Fallback to the default API endpoint: ${failure.message}")
        uri("http://localhost")

      case Right(result: Uri) =>
        result
    })

  private
  def toUri(baseUri: Uri, mode: ParserFactoryMode, source: String) =
    baseUri +?("parser_factory_mode", mode) +?("source", source)

  def resource[F[_] : ConcurrentEffect](
    implicit executionContext: ExecutionContext
  ): Resource[F, PhpToJsonParser[F]] =
    BlazeClientBuilder[F](executionContext)
      .resource
      .flatMap(client => liftF(memoizeApi.map(api => (mode, source) =>
        api >>= (baseUri => client.get(toUri(baseUri, mode, source))(_.body.pure))
      )))
}
