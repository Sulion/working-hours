import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    application
}

group = "io.github.sulion"
version = "1.0-SNAPSHOT"
application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val ktorVersion = "1.2.2"
    val logbackVersion = "1.2.1"
    fun ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"
    fun ktor() = "io.ktor:ktor:$ktorVersion"

    compile(kotlin("stdlib"))
    compile(ktor())
    compile(ktor("server-netty"))
    compile("ch.qos.logback:logback-classic:$logbackVersion")
    compile("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<Test> {
    useJUnitPlatform {}
}