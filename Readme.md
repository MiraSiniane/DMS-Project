# Backend Services — Quick Start with Docker

This guide shows how to build and run all backend services (Auth, Storage, Document, Gateway, and PostgreSQL) using Docker Compose.

## Prerequisites

* Docker & Docker Compose installed
* (Optional) `.env` file at project root with any overrides

## Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-org/dms-backend.git
   cd dms-backend
   ```

2. **(Optional) Create an `.env` file**
   To override defaults, copy and edit:

   ```bash
   cp .env.example .env
   ```

   The default `.env.example` sets:

   ```env
   POSTGRES_USER=entreprise_dms
   POSTGRES_PASSWORD=hanalynamira
   POSTGRES_DB=entreprise_dms
   ```

3. **Launch all services**

   ```bash
   docker-compose up --build -d
   ```

4. **Verify services are healthy**
   Wait a minute, then run:

   ```bash
   curl -s -o /dev/null -w "%{http_code}" http://localhost:8084/actuator/health   # Auth
   curl -s -o /dev/null -w "%{http_code}" http://localhost:8083/actuator/health   # Storage
   curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/actuator/health   # Document
   curl -s -o /dev/null -w "%{http_code}" http://localhost:8088/actuator/health   # Gateway
   ```

   Each should return **200**.

## Stopping

```bash
docker-compose down
```

## Logs & Troubleshooting

* **View logs**

  ```bash
  docker-compose logs -f auth-service
  docker-compose logs -f storage-service
  docker-compose logs -f document-service
  docker-compose logs -f gateway
  ```
* **Rebuild a single service**

  ```bash
  docker-compose build document-service
  docker-compose up -d document-service
  ```


