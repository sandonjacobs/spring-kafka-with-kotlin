plugins {
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    implementation("com.google.protobuf:protobuf-kotlin:3.25.1")
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpckt")
            }
            task.builtins {
                create("kotlin")
            }
            // Do not remove Java builtin, Kotlin codegen depends on it
        }
    }
}

tasks.test {
    useJUnitPlatform()
} 