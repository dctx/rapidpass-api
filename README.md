rapidpass-api
-------------

For details about this proeject please visit https://gitlab.com/dctx/rapidpass-api/-/wikis/About-this-project

## Prerequisite


## Running the Application via Docker

To build the from source, run this, only needed now since we have external dependencies not on a maven repo:

```
./mvnw package
docker-compose build
```

Running the application via docker:

```
# download latest
docker pull dctx/rapidpass-api:latest

# create the containers
docker-compose up -d
```

To stop/start the application, run:

```
# stop
docker-compose stop

# start
docker-compose start
```

To cleanup:

```
docker-compose down
```

The OpenAPI spec can be accessed via: [http://localhost:9999](http://localhost:9999)
The API doc can be accessed via [http://localhost:8080/api/v1](http://localhost:8080/api/v1)

## Running the Application via Maven

To run the application:

```
## start a postgres database
docker-compose up -d db

## run the application
./mvnw spring-boot:run
```
The API doc can be accessed via [http://localhost:8080](http://localhost:8080)

Note: this requires postgres now as some SQL statements are no longer supported by postgres

## Packaging Application

To package the application, run the following:

```
./mvnw package
```