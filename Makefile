# Makefile for Parking Garage System

.PHONY: start-kafka stop-kafka generate-classes unit-test build run open-app destroy startup help

# Start Kafka and related services using Docker Compose
start-kafka:
	docker compose up -d

# Stop Kafka and related services, cleaning up containers
stop-kafka:
	docker compose down

# Generate Java/Kotlin classes from protobuf schemas using Gradle
generate-classes:
	./gradlew generateProto

# Run all unit tests
unit-test:
	./gradlew test

# Build the application
build:
	./gradlew build

# Run the application using Spring Boot's bootRun
run:
	./gradlew bootRun

# Open the application in your default browser
open-app:
	open http://localhost:8080/

# Stop Kafka and clean the Gradle build
# (useful for a full cleanup)
destroy: stop-kafka
	./gradlew clean

# Start Kafka, clean, generate protobuf classes, and run the app
startup: start-kafka destroy generate-classes run

# Show help for each target
default: help
help:
	@echo "\nAvailable targets:"
	@echo "  🚀  start-kafka      Start Kafka and related services using Docker Compose"
	@echo "  🛑  stop-kafka       Stop Kafka and related services, cleaning up containers"
	@echo "  ⚡  startup          Start Kafka, clean, generate protobuf classes, and run the app"
	@echo "  🛠️  generate-classes Generate Java/Kotlin classes from protobuf schemas using Gradle"
	@echo "  🧪  unit-test        Run all unit tests"
	@echo "  🏗️  build            Build the application"
	@echo "  ▶️  run              Run the application using Spring Boot's bootRun"
	@echo "  🌐  open-app         Open the application in your default browser"
	@echo "  💣  destroy          Stop Kafka and clean the Gradle build (full cleanup)"
	@echo "  📖  help             Show this help message\n" 