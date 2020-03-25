# Doing the maven build here so we can are not dependent on the java compiler of the
# host machine. This compiles the jar.
FROM openjdk:8-jdk-alpine as build

ARG VERSION=0.0.1-SNAPSHOT

# running once to download dependencies. Doing this here for caching. As long as pom.xml
# is not changed, the dependencies should not change. This should speed up the builds
ADD pom.xml pom.xml
ADD .mvn .mvn
ADD mvnw mvnw
RUN ./mvnw dependency:go-offline 
# building jar. skipping test. this should have been done before building the image
ADD src src
RUN ./mvnw package -DskipTests && mv target/api-${VERSION}.jar target/api.jar

# Just copy the Jar and run it. No extra stuff from maven. Should help with the image size
FROM openjdk:8-jdk-alpine
COPY --from=build /target/api.jar /api.jar
ENTRYPOINT [ "java", "-jar", "/api.jar", "--spring.profiles.active=docker" ] 