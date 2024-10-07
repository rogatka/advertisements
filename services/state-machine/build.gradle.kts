plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

repositories {
    mavenCentral()
}

val springStateMachineVersion = "4.0.0"
val springStateMachineKryoVersion = "4.0.0"

dependencies {
    implementation(project(":services:common"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.mongock:mongock-springboot-v3")
    implementation("io.mongock:mongodb-reactive-driver")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.statemachine:spring-statemachine-core:$springStateMachineVersion")
    implementation("org.springframework.statemachine:spring-statemachine-kryo:$springStateMachineKryoVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation ("io.github.microutils:kotlin-logging-jvm:3.0.5")
}
