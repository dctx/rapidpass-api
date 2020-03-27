# # Doing the maven build here so we can are not dependent on the java compiler of the
# # host machine. This compiles the jar.
# FROM maven:3.6-jdk-8-openj9 as build

# # running once to download dependencies. Doing this here for caching. As long as pom.xml
# # is not changed, the dependencies should not change. This should speed up the builds
# ADD pom.xml pom.xml
# RUN mvn dependency:go-offline 
# # building jar. skipping test. this should have been done before building the image
# ADD src src
# RUN mvn package -DskipTests

## Temporarily build outside docker for now until we resolve external dependencies

# Just copy the Jar and run it. No extra stuff from maven. Should help with the image size
FROM openjdk:8-jdk-alpine
# COPY --from=build /target/api.jar /api.jar
COPY ./target/rapidpass-api.jar /rapidpass-api.jar
ENTRYPOINT [ "java", "-jar", "/rapidpass-api.jar", "--spring.profiles.active=docker" ] 