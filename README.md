# ✈️ Airline Passenger Service System

An enterprise-grade Airline Passenger Service System built using Java 17, Spring Boot Microservices, and Spring Cloud.

This project is designed to simulate a real-world airline backend similar to systems used by major airlines. It demonstrates modern microservices architecture, secure REST APIs, event-driven communication, containerization, and cloud-native deployment.

---

## 📌 Project Objectives

- Learn Enterprise Java Development
- Master Spring Boot Microservices
- Implement Spring Cloud Components
- Build Production-Ready REST APIs
- Understand Distributed System Design
- Practice CI/CD and Containerization
- Prepare for Java Backend Developer Interviews

---

## 🏗️ Architecture

```
                     Client Applications
                             │
                             ▼
                     API Gateway
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
  Flight Service     Passenger Service    Booking Service
        │                    │                    │
        └──────────────┬─────┴──────────────┐
                       ▼                    ▼
               Check-in Service     Seat Inventory
                       │
                       ▼
               Baggage Service
                       │
                       ▼
                Payment Service
                       │
                       ▼
              Notification Service

                 Eureka Discovery Server
                 Config Server
                 Kafka Message Broker
```

---

## 🚀 Technology Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Cloud
- Spring Security
- Spring Data JPA
- Hibernate

### Database

- PostgreSQL
- Redis

### Messaging

- Apache Kafka

### DevOps

- Docker
- Kubernetes
- Maven
- Git
- GitHub

### Monitoring

- Spring Boot Actuator
- Prometheus
- Grafana

---

## 📦 Planned Microservices

- Discovery Server (Eureka)
- Config Server
- API Gateway
- Authentication Service
- Flight Service
- Passenger Service
- Booking Service
- Seat Inventory Service
- Check-in Service
- Baggage Service
- Payment Service
- Notification Service

---

## 📂 Project Structure

```
airline-passenger-system
│
├── discovery-server
├── config-server
├── api-gateway
├── auth-service
├── flight-service
├── passenger-service
├── booking-service
├── seat-inventory-service
├── checkin-service
├── baggage-service
├── payment-service
├── notification-service
│
├── database
├── docker
├── docs
├── kubernetes
├── postman
└── scripts
```

---

## 🛠️ Features

- JWT Authentication
- API Gateway Routing
- Service Discovery
- Distributed Configuration
- Circuit Breaker
- Load Balancing
- Event-Driven Architecture
- Asynchronous Messaging
- Logging & Monitoring
- Docker Support
- Kubernetes Deployment

---

## 📅 Development Roadmap

### Phase 1
- Parent Project
- Discovery Server
- Config Server
- API Gateway

### Phase 2
- Authentication Service
- JWT Security

### Phase 3
- Flight Service
- Passenger Service

### Phase 4
- Booking Service
- Seat Inventory

### Phase 5
- Check-in
- Baggage
- Payment
- Notification

### Phase 6
- Kafka Integration
- Redis Caching
- Docker
- Kubernetes
- Monitoring

---

## 👨‍💻 Author

Mahesh Karambalkar

Learning Project for Enterprise Java, Spring Boot, Microservices, and Cloud-Native Development.