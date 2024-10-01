FROM gradle:8.8.0-jdk21 AS build
WORKDIR /mme-api
COPY . /mme-api
RUN gradle build

FROM eclipse-temurin:21
WORKDIR /mme-api
COPY --from=build /mme-api/build/libs/api-0.1.0-SNAPSHOT.jar .
COPY dev-data ./dev-data/
EXPOSE 8080
CMD ["java", "-jar", "api-0.1.0-SNAPSHOT.jar", "--spring.profiles.active=dev"]
