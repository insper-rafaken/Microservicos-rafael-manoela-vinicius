FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app

# instala store:product:1.0.0
COPY api/product/product/pom.xml ./api/product/product/pom.xml
COPY api/product/product/src     ./api/product/product/src
RUN cd api/product/product && mvn install -DskipTests -q

# builda o product-service
COPY api/product/product-service/pom.xml ./api/product/product-service/pom.xml
COPY api/product/product-service/src     ./api/product/product-service/src
RUN cd api/product/product-service && mvn clean package -DskipTests -q

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/api/product/product-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
