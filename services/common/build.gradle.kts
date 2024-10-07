plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("io.spring.dependency-management") version "1.1.6"
}

repositories {
    mavenCentral()
}

val tikaVersion = "2.9.2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.mongock:mongodb-reactive-driver")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.apache.tika:tika-core:$tikaVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation ("io.github.microutils:kotlin-logging-jvm:3.0.5")
}