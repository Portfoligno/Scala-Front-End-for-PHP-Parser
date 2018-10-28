plugins {
  scala
  id("com.github.johnrengelman.shadow") version "4.0.2"
}
val scalaCompilerPlugin: Configuration = configurations.create("scalaCompilerPlugin")

tasks.getByName<Wrapper>("wrapper") {
  gradleVersion = "4.10.2"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  jcenter()
}
dependencies {
  implementation("org.scala-lang:scala-library:2.12.7")
  scalaCompilerPlugin("org.scalamacros:paradise_2.12.7:2.1.1")
  implementation("org.http4s:http4s-blaze-client_2.12:0.19.0")
  implementation("org.http4s:http4s-dsl_2.12:0.19.0")
  implementation("org.http4s:http4s-circe_2.12:0.19.0")
  implementation("io.circe:circe-generic_2.12:0.10.0")
  implementation("is.cir:ciris-cats-effect_2.12:0.11.0")

  testImplementation("org.scalacheck:scalacheck_2.12:1.14.0")
  testImplementation("org.slf4j:slf4j-simple:1.7.25")
}

tasks.withType<ScalaCompile> {
  scalaCompileOptions.additionalParameters =
      listOf("-Xplugin:" + scalaCompilerPlugin.asPath)
}
