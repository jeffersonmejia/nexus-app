# Build the React frontend.
FROM node:20-alpine AS frontend-build
WORKDIR /workspace/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# Package the Spring Boot app together with the compiled frontend.
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
COPY src/ src/
COPY --from=frontend-build /workspace/frontend/dist/ frontend/dist/
RUN chmod +x mvnw && ./mvnw -B -DskipTests package

# Run as a non-root user in a small Java runtime image.
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
RUN useradd --system --uid 10001 --create-home appuser
COPY --from=backend-build /workspace/target/nexus-0.0.1-SNAPSHOT.jar /app/app.jar
USER 10001
EXPOSE 8082
ENV SERVER_PORT=8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
