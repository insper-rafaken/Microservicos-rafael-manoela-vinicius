FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app

# instala a dependência local store:product:1.0.0
COPY api/product/pom.xml ./api/product/pom.xml
COPY api/product/src     ./api/product/src
RUN cd api/product && mvn install -DskipTests -q

# builda o product-service
COPY api/product-service/pom.xml ./api/product-service/pom.xml
COPY api/product-service/src     ./api/product-service/src
RUN cd api/product-service && mvn clean package -DskipTests -q

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/api/product-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
