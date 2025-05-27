# Parking Garage System

A multi-module project demonstrating the use of Spring Kafka with Kotlin for a parking garage management system.

## Project Structure

- `model`: Contains Protobuf definitions for the parking garage data model
- `data-generator`: Generates and publishes parking events to Kafka
- `parking-service`: Spring Boot microservice that consumes Kafka events and provides REST endpoints

## Requirements

- Java 17
- Gradle 8.5+
- Apache Kafka
- Protobuf compiler

## Building the Project

```bash
./gradlew build
```

## Running the Services

1. Start Apache Kafka
2. Run the data generator:
   ```bash
   ./gradlew :data-generator:run
   ```
3. Run the parking service:
   ```bash
   ./gradlew :parking-service:bootRun
   ```

## Development

This project uses:
- Kotlin 1.9.22
- Spring Boot 3.2.3
- Spring Kafka
- Protocol Buffers
- Gradle with Kotlin DSL
