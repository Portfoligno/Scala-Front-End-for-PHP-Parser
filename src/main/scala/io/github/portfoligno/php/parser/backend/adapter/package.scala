package io.github.portfoligno.php.parser.backend

import cats.Applicative
import cats.effect.Resource
import ciris.{ConfigDecoder, ConfigError}
import org.http4s.Uri
import fs2.Stream

package object adapter {
  private[adapter] lazy val log: org.log4s.Logger = org.log4s.getLogger

  implicit lazy val uriConfigDecoder: ConfigDecoder[String, Uri] =
    ConfigDecoder.fromTry(Uri.getClass.getName)((Uri.fromString _).andThen(_.toTry))

  def env[F[_] : Applicative, Value](key: String)(
    implicit decoder: ConfigDecoder[String, Value]
  ): F[Either[ConfigError, Value]] = {
    // Adapt Cats' Applicative as Ciris'
    import ciris.cats.effect._

    ciris.envF[F, Value](key).value
  }


  // Ad-hoc helpers to avoid relying on partial-unification for syntax highlighting
  private[adapter]
  implicit class ResourceOps[F[_], A](private val resource: Resource[F, A]) extends AnyVal {
    def map[B](f: A => B)(implicit F: Applicative[F]): Resource[F, B] =
      resource.flatMap(a => Resource.pure[F, B](f(a)))

    def >>=[B](f: A => Resource[F, B]): Resource[F, B] =
      resource.flatMap(f)
  }

  private[adapter]
  implicit class StreamOps[F[_], A](stream: Stream[F, A]) {
    def >>=[B](f: A => Stream[F, B]): Stream[F, B] =
      stream.flatMap(f)
  }
}
