# Docker Deployment Guide for Account Management

This guide explains how to build and run the Account Management application using Docker.

## Prerequisites

- Docker Engine 20.10+ 
- Docker Compose 2.0+ (optional, for docker-compose setup)
- PostgreSQL database (if not using docker-compose)

## Quick Start

### Option 1: Using Docker Compose (Recommended)

1. **Copy the example environment file:**
   ```bash
   cp env.example .env
   ```

2. **Edit `.env` file and set required variables:**
   ```bash
   # Required variables
   SPRING_DATASOURCE_PASSWORD=your-secure-password
   JWT_SECRET=your-secret-key-at-least-32-characters-long
   
   # Optional: customize other settings
   SPRING_DATASOURCE_USERNAME=postgres
   SERVER_PORT=8080
   ```

3. **Copy docker-compose example:**
   ```bash
   cp docker-compose.example.yml docker-compose.yml
   ```

4. **Start the application:**
   ```bash
   docker-compose up -d
   ```

5. **Check logs:**
   ```bash
   docker-compose logs -f account-management
   ```

6. **Verify health:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Option 2: Manual Docker Build and Run

1. **Build the Docker image:**
   ```bash
   docker build -t account-management:latest .
   ```

2. **Create a `.env` file** (see env.example for reference):
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/accountdb
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=your-password
   JWT_SECRET=your-secret-key-at-least-32-characters-long
   SERVER_PORT=8080
   ```

3. **Run the container:**
   ```bash
   docker run -d \
     --name account-management \
     --env-file .env \
     -p 8080:8080 \
     -v $(pwd)/logs:/app/logs \
     account-management:latest
   ```

4. **Check logs:**
   ```bash
   docker logs -f account-management
   ```

## Required Environment Variables

### Critical (Must be set)

- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
  - Format: `jdbc:postgresql://host:port/database`
  - Example: `jdbc:postgresql://postgres:5432/accountdb`

- `SPRING_DATASOURCE_USERNAME` - Database username
  - Default: `postgres`

- `SPRING_DATASOURCE_PASSWORD` - Database password
  - **REQUIRED** - No default value

- `JWT_SECRET` - JWT secret key for token signing
  - **REQUIRED** - Must be at least 256 bits (32 characters)
  - Generate securely: `openssl rand -base64 32`

### Optional (Have defaults)

- `SPRING_PROFILES_ACTIVE` - Spring profile (default: `prod`)
- `SERVER_PORT` - Application port (default: `8080`)
- `JWT_ACCESS_TOKEN_EXPIRATION` - Access token TTL in ms (default: `3600000` = 1 hour)
- `JWT_REFRESH_TOKEN_EXPIRATION` - Refresh token TTL in ms (default: `86400000` = 24 hours)
- `LOG_FILE_PATH` - Log file path (default: `./logs/account-management.log`)
- `JAVA_OPTS` - JVM options (default: `-Xmx512m -Xms256m`)

## Database Setup

### Using Docker Compose

The `docker-compose.yml` includes a PostgreSQL service that will be automatically set up.

### Using External PostgreSQL

1. Ensure PostgreSQL is running and accessible
2. Create the database:
   ```sql
   CREATE DATABASE accountdb;
   ```
3. Update `SPRING_DATASOURCE_URL` in your `.env` file
4. Liquibase will automatically run migrations on startup

## Health Checks

The application includes health check endpoints:

- **Health endpoint:** `http://localhost:8080/actuator/health`
- **Info endpoint:** `http://localhost:8080/actuator/info`

Docker health checks are configured to use these endpoints automatically.

## Logging

Logs are written to:
- **Container:** `/app/logs/account-management.log`
- **Host (if volume mounted):** `./logs/account-management.log`

View logs:
```bash
# Docker Compose
docker-compose logs -f account-management

# Docker
docker logs -f account-management

# Log file (if volume mounted)
tail -f logs/account-management.log
```

## Troubleshooting

### Container won't start

1. **Check logs:**
   ```bash
   docker logs account-management
   ```

2. **Verify database connection:**
   - Ensure PostgreSQL is running and accessible
   - Check `SPRING_DATASOURCE_URL` is correct
   - Verify credentials in `.env` file

3. **Check environment variables:**
   ```bash
   docker exec account-management env
   ```

### Health check failing

1. **Check if application is running:**
   ```bash
   docker ps
   ```

2. **Test health endpoint manually:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

3. **Check application logs for errors**

### Database connection issues

1. **Verify database is accessible:**
   ```bash
   # From host
   psql -h localhost -U postgres -d accountdb
   
   # From container
   docker exec -it account-management-db psql -U postgres -d accountdb
   ```

2. **Check network connectivity** (if using docker-compose):
   ```bash
   docker network inspect account-management-network
   ```

## Security Best Practices

1. **Never commit `.env` file** - It contains sensitive credentials
2. **Use strong JWT secret** - Generate with: `openssl rand -base64 32`
3. **Use strong database passwords** - At least 16 characters, mixed case, numbers, symbols
4. **Limit network exposure** - Only expose necessary ports
5. **Use secrets management** - Consider Docker secrets or external secret managers for production
6. **Regular updates** - Keep base images and dependencies updated

## Production Deployment

For production deployment:

1. **Use environment-specific secrets management**
2. **Set up proper monitoring and alerting**
3. **Configure log aggregation**
4. **Set up backup strategy for database**
5. **Use reverse proxy** (nginx, traefik) for SSL termination
6. **Configure resource limits** in docker-compose or Kubernetes
7. **Set up CI/CD pipeline** for automated deployments

Example resource limits in docker-compose:
```yaml
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 1G
    reservations:
      cpus: '0.5'
      memory: 512M
```

## Building for Different Architectures

The Dockerfile uses multi-stage builds optimized for Linux/amd64. For other architectures:

```bash
# Build for ARM64
docker buildx build --platform linux/arm64 -t account-management:latest .

# Build for multiple platforms
docker buildx build --platform linux/amd64,linux/arm64 -t account-management:latest .
```

## Stopping and Cleaning Up

```bash
# Stop containers
docker-compose down

# Stop and remove volumes (WARNING: deletes database data)
docker-compose down -v

# Remove image
docker rmi account-management:latest
```

