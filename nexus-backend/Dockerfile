# Stage 1: Build JAR using Maven & JDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper & POM
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies offline for caching
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Production Lightweight JRE 21 Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root system user for OWASP container security
RUN addgroup -S nexushr && adduser -S nexushr -G nexushr
USER nexushr

# Copy compiled JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
