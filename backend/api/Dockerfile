# ---------- build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# copiar primero pom.xml
COPY pom.xml .

# descargar dependencias (se cachea esta capa)
RUN mvn -B dependency:go-offline

# ahora copiar el código
COPY src ./src

# compilar
RUN mvn clean package -DskipTests

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]