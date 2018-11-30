plugins {
  maven
  scala
  `java-library`
  id("com.github.johnrengelman.shadow") version "4.0.2"
}
val scalaCompilerPlugin: Configuration = configurations.create("scalaCompilerPlugin")

tasks.getByName<Wrapper>("wrapper") {
  gradleVersion = "4.10.2"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
  // Combine dependencies from the 'shadow' configuration instead!
  configurations = listOf(project.configurations.getByName("shadow"))

  artifacts {
    add("archives", this@withType)
  }
  archiveName = "$baseName-$version.$extension"

  relocate("io.circe", "io.github.portfoligno.php.parser.backend.circe")
  relocate("org.http4s.circe", "io.github.portfoligno.php.parser.backend.http4s.circe")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  jcenter()
  maven("https://jitpack.io")
}
dependencies {
  scalaCompilerPlugin("org.scalamacros:paradise_2.12.7:2.1.1")
  api("org.scala-lang:scala-library:2.12.7")
  api("co.fs2:fs2-io_2.12:1.0.0")

  implementation("org.http4s:http4s-blaze-client_2.12:0.19.0")
  implementation("org.http4s:http4s-dsl_2.12:0.19.0")
  implementation("is.cir:ciris-cats-effect_2.12:0.11.0")

  shadow("org.http4s:http4s-circe_2.12:0.19.0") { setTransitive(false) }
  shadow("io.circe:circe-fs2_2.12:0.10.0") { setTransitive(false) }
  shadow("io.github.sd-yip.circe:circe-jawn_2.12:d0923ca4") { setTransitive(false) }
  implementation("org.http4s:http4s-jawn_2.12:0.19.0")
  implementation("org.spire-math:jawn-parser_2.12:0.13.0")

  shadow("io.github.sd-yip.circe:circe-generic-extras_2.12:d0923ca4") { setTransitive(false) }
  shadow("io.github.sd-yip.circe:circe-generic_2.12:d0923ca4") { setTransitive(false) }
  implementation("com.chuusai:shapeless_2.12:2.3.3")

  shadow("io.github.sd-yip.circe:circe-core_2.12:d0923ca4") {
    exclude("org.scala-lang")
    exclude("org.typelevel")
  }

  testImplementation("org.scalacheck:scalacheck_2.12:1.14.0")
  testImplementation("org.slf4j:slf4j-simple:1.7.25")
}

tasks.withType<ScalaCompile> {
  scalaCompileOptions.additionalParameters = listOf(
      "-Xplugin:" + scalaCompilerPlugin.asPath,
      "-Ypartial-unification",
      "-language:higherKinds")

  scalaCompileOptions.forkOptions.jvmArgs!!.addAll(arrayOf(
      "-Xss32m",
      "-Xmx8g",
      "-XX:MaxJavaStackTraceDepth=-1"))
}
