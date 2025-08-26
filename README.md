# Feature Flags & Movie Search Application

A microservices-based application demonstrating feature flag management and movie search capabilities with real-time flag synchronization.

## Architecture Overview

### Services
- **Feature Flag Service**: Manages feature flags with CRUD operations and real-time notifications
- **Movie Search Service**: Searches movies via OMDB API and respects feature flags
- **RabbitMQ**: Message broker for reliable async communication between services

### Technology Stack
- **Backend**: Spring Boot 3.x with Java 17
- **Frontend**: React 18 with TypeScript
- **Database**: PostgreSQL 15
- **Message Broker**: RabbitMQ 3
- **Testing**: JUnit 5, Testcontainers, React Testing Library
- **Containerization**: Docker & Docker Compose

## Design Decisions

### 1. Microservices Architecture
- **Separation of Concerns**: Each service has a single responsibility
- **Independent Deployment**: Services can be deployed and scaled independently
- **Technology Flexibility**: Each service can use optimal technologies

### 2. Event-Driven Communication
- **RabbitMQ**: Reliable message broker with delivery guarantees for flag updates
- **Async Processing**: Non-blocking flag propagation with message persistence
- **Loose Coupling**: Services communicate without direct dependencies
- **Dead Letter Queues**: Handle failed message processing gracefully

### 3. Feature Flag Design
- **Boolean Flags Only**: Simple but extensible design
- **Real-time Updates**: Immediate propagation to all consumers
- **Database Persistence**: Reliable storage with ACID properties

### 4. Testing Strategy
- **Unit Tests**: Core business logic
- **Integration Tests**: API endpoints and database operations
- **E2E Tests**: Complete user workflows
- **Testcontainers**: Isolated test environments

## Prerequisites

- Docker & Docker Compose
- Java 17+
- Node.js 18+
- Maven 3.8+

## Quick Start

1. **Clone and Setup**
   ```bash
   git clone https://github.com/armando-monjane/knime-code-challenge.git
   cd knime-code-challenge
   ```

2. **Run with Docker Compose**
   ```bash
   cp .env.example .env
   docker-compose up -d
   ```

3. **Access Applications**
   - Feature Flag App: http://localhost:3000
   - Movie Search App: http://localhost:3001
   - Feature Flag API: http://localhost:8080
   - Movie Search API: http://localhost:8081

## Development Setup

### Backend Services

1. **Feature Flag Service**
   ```bash
   cd feature-flag-service
   mvn spring-boot:run
   ```

2. **Movie Search Service**
   ```bash
   cd movie-search-service
   mvn spring-boot:run
   ```

### Frontend Applications

1. **Feature Flag Frontend**
   ```bash
   cd feature-flag-frontend
   npm install
   npm start
   ```

2. **Movie Search Frontend**
   ```bash
   cd movie-search-frontend
   npm install
   npm start
   ```

### Frontend Features

**Feature Flag Frontend:**
- List all feature flags with status indicators
- Add new feature flags with validation
- Edit existing feature flags
- Toggle flag status (enable/disable)
- Delete feature flags with confirmation
- Real-time updates and error handling
- Modern, responsive UI with Tailwind CSS

**Movie Search Frontend:**
- Search movies by title using OMDB API
- Display search results with movie posters, titles, and years
- Dark mode theme support (controlled by feature flag)
- Maintenance mode handling with user-friendly message
- Responsive grid layout for movie cards
- IMDB links for each movie
- Real-time feature flag polling
- Modern, responsive UI with Tailwind CSS


## API Documentation

### Feature Flag API
- `GET /api/flags` - List all flags
- `POST /api/flags` - Create new flag
- `PUT /api/flags/{id}` - Update flag
- `DELETE /api/flags/{id}` - Delete flag
- `PATCH /api/flags/{id}/toggle` - Toggle flag status

### Movie Search API
- `GET /api/movies/search?title={title}` - Search movies
- `GET /api/movies/health` - Health check

## Feature Flags

The application supports two feature flags:
- `dark_mode`: Toggles dark/light theme in movie search frontend
- `maintenance_mode`: Enables maintenance mode (503 responses)

## Monitoring & Observability

- **Health Checks**: `/actuator/health` endpoints





