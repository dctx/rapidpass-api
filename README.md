rapidpass-api
-------------

For details about this proeject please visit https://gitlab.com/dctx/rapidpass-api/-/wikis/About-this-project

## Prerequisite


## Running the Application via Docker

Running the application via docker:

```
docker-compose build
docker-compose up -d
```

The API doc can be accessed via [http://localhost:8080](http://localhost:8080)

## Running the Application via Maven

To run the application:

```
./mvnw spring-boot:run
```
The API doc can be accessed via [http://localhost:8080](http://localhost:8080)

Note: that this will use the default H2 database. To use postgres, pass the following flag: `-Drun.profile=postgres`

## Packaging Application

To package the application, run the following:

```
./mvnw package
```