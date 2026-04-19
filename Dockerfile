# -------- Stage 1: Build --------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 1. Copy only pom.xml first
COPY pom.xml .

# 2. Download dependencies
RUN mvn -B -q -e -DskipTests dependency:go-offline

# 3. Copy source code
COPY src ./src

# 4. Build the application
RUN mvn -B -DskipTests package

# -------- Stage 2: Runtime --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
