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
    val jacksonVersion = "2.9.8"
    fun ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"
    fun ktor() = "io.ktor:ktor:$ktorVersion"
    compile(kotlin("stdlib"))
    compile(ktor())
    compile(ktor("server-netty"))
    compile("ch.qos.logback:logback-classic:$logbackVersion")
    compile( "com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    compile( "com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    compile( "com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    compile( "com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    compile( "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "12"
}
tasks.withType<Test> {
    useJUnitPlatform {}
}