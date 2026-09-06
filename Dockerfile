## Build Stage ##
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests=true

## Run Stage ##
FROM alpine/java:17-jre

WORKDIR /run

RUN adduser -D shoeshop

COPY --from=build /app/target/shoeshop-0.0.1-SNAPSHOT.jar /run/app.jar

RUN chown -R shoeshop:shoeshop /run

USER shoeshop

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]
