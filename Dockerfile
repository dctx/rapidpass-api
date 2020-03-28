# Doing the maven build here so we can are not dependent on the java compiler of the
# host machine. This compiles the jar.
FROM openjdk:15-jdk-alpine as build

# building jar. doing test too
ADD . .
RUN ./mvnw test package

# Just copy the Jar and run it. No extra stuff from maven. Should help with the image size
FROM openjdk:15-jdk-alpine
COPY --from=build /target/rapidpass-api.jar /rapidpass-api.jar
ENTRYPOINT [ "java", "-jar", "/rapidpass-api.jar", "--spring.profiles.active=env" ] 
