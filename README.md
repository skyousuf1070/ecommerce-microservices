# 🛒 E-Commerce Microservices Ecosystem

A distributed, event-driven e-commerce platform built with **Spring Boot 3**, **Apache Kafka**, and **MySQL**, managed as a Dockerized monorepo.

---

## 🏗 Architecture Overview

The system uses a choreographic EDA (Event-Driven Architecture) where services communicate asynchronously via Kafka to ensure high availability and decoupling.



### Services & Responsibilities
* **Order Service (`:8080`)**: Entry point for checkout. Orchestrates order creation and publishes `order-placed` events.
* **Cart Service (`:8081`)**: Manages temporary shopping sessions and persists cart data to MySQL.
* **Payment Service (`:8082`)**: Consumer-only service that processes payments based on Kafka events.
* **Infrastructure**:
    * **Kafka/Zookeeper**: Message broker for inter-service communication.
    * **MySQL 8.0**: Persistent storage with automatic schema generation.

---

## 🛠 Tech Stack
* **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA
* **Messaging:** Apache Kafka (Confluent Platform 7.5.0)
* **Database:** MySQL 8.0
* **DevOps:** Docker, Docker Compose, Maven

---

## 🚀 Getting Started

### 1. Prerequisites
* Docker Desktop
* Maven 3.8+ (for local builds)
* Ensure ports `8080`, `8081`, `8082`, `3306`, and `9092` are free.

### 2. Build & Deploy
From the root directory, run the following commands:

```bash
# Build the application artifacts
mvn clean package -DskipTests

# Launch the entire stack in detached mode
docker compose up -d --build

