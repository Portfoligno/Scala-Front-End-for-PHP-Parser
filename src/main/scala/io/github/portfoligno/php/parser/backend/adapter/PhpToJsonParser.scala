package io.github.portfoligno.php.parser.backend.adapter

import cats.effect.Concurrent.memoize
import cats.effect.{ConcurrentEffect, Resource}
import cats.syntax.functor._
import fs2.Stream
import org.http4s.{Method, Request, Uri}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

trait PhpToJsonParser[F[_]] {
  def parse(mode: ParserFactoryMode, source: String): Stream[F, Byte]
}

object PhpToJsonParser {
  private
  def apiSource[F[_] : ConcurrentEffect]: F[F[Uri]] =
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

  private
  def fetch[F[_]](client: Client[F], method: Method, uri: Uri): Stream[F, Byte] =
    Stream
      .resource(client.run(Request[F](method, uri)))
      .map(_.body)
      .flatten

  def resource[F[_] : ConcurrentEffect](
    implicit executionContext: ExecutionContext
  ): Resource[F, PhpToJsonParser[F]] =
    BlazeClientBuilder[F](executionContext).resource >>= (client =>
      Resource.liftF(apiSource.map(api =>
        (mode, source) =>
          Stream.eval(api) >>= (baseUri => fetch(client, Method.GET, toUri(baseUri, mode, source)))
      )))
}
