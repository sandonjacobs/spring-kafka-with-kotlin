plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.serialization") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.5.0"
  id("io.spring.dependency-management") version "1.1.7"
  id("com.google.protobuf") version "0.9.4"

}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

group = "com.example.parking"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

springBoot {
    mainClass.set("com.example.parking.ParkingServiceApplicationKt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.apache.kafka:kafka-streams")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")


    // Logging dependencies
    implementation("ch.qos.logback:logback-classic")
    implementation("org.slf4j:slf4j-api")

    // Confluent dependencies for Protobuf
    implementation("io.confluent:kafka-protobuf-serializer:7.9.1")
    implementation("io.confluent:kafka-protobuf-provider:7.9.1")
    implementation("io.confluent:kafka-streams-protobuf-serde:7.9.1")

    // SpringDoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    // Protobuf
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    // implementation("com.google.protobuf:protobuf-kotlin:3.25.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.awaitility:awaitility:4.3.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}