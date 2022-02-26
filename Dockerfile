# If non M1 chip use:
# FROM amazoncorretto:11-alpine-jdk

FROM arm64v8/openjdk:11-jdk
COPY build/libs/metrics-0.0.1-SNAPSHOT.jar metrics-app.jar
ENTRYPOINT ["java","-jar","/metrics-app.jar"]
