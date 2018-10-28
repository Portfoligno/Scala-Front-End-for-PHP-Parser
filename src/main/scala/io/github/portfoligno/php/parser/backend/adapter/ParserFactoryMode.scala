package io.github.portfoligno.php.parser.backend.adapter

import org.http4s.{QueryParamEncoder, QueryParameterValue}

sealed trait ParserFactoryMode

object ParserFactoryMode {
  object PREFER_PHP7 extends ParserFactoryMode
  object PREFER_PHP5 extends ParserFactoryMode
  object ONLY_PHP7 extends ParserFactoryMode
  object ONLY_PHP5 extends ParserFactoryMode

  implicit lazy val paramEncoder: QueryParamEncoder[ParserFactoryMode] =
    value => QueryParameterValue(value.getClass.getSimpleName.dropRight(1))
}
