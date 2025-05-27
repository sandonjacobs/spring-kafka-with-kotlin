plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    implementation(project(":model"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.apache.kafka:kafka-clients:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("org.slf4j:slf4j-api:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("org.springframework.kafka:spring-kafka:3.1.1")
    implementation("io.confluent:kafka-protobuf-serializer:7.9.1")
    implementation("io.confluent:kafka-schema-registry-client:7.9.1")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.1.1")
} 