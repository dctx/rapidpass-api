rapidpass-api
-------------

For details about the project, please visit our [wiki](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/wikis/home).

## Prerequisites

1. **Apache Maven** `version 3.6.3` 
2. **Java** `version 1.8.0_65`
3. **Docker** `version 19.03.8`


## Running the Application via Docker

#### Docker build

To build the RapidPass API from source, needed now since we have external dependencies not on a maven repo:

```
./mvnw package
docker-compose build
```

#### Run using docker

To run the application via docker:

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

### Keycloak

#### Setup Keycloak locally

1. Access your local keycloak instance `http://localhost:8180/auth`.
2. Log in with default username and password: `admin` and `admin`.
3. Import the local backup of the realm. Click `Add Realm`.
4. Click `Import` and select file `src/resources/keycloak/realm-export.json`.
5. Create a new user in the `rapidpass` realm with username `scanner-registrar@rapidpass.ph` and password 
    `scanner-registrar@rapidpass.ph`. This will be used by the API server to create a new scanner device 
    in Keycloak.

This sets up the following:
* `rapidpass` realm
* `rapidpass-dashboard` client (for the dashboard application)
* `rapidpass-api` client (for the API server)

For more details related on authentication, check our 
[wiki on authentication](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/wikis/guide/Authentication). 

To cleanup:
```
docker-compose down
```

The OpenAPI spec can be accessed via: [http://localhost:9999](http://localhost:9999).
The API doc can be accessed via [http://localhost:8080/api/v1](http://localhost:8080/api/v1).

## Running the Application via Maven

To run the application:

```
## start a postgres database
docker-compose up -d db

## run the application
./mvnw spring-boot:run
```
The API doc can be accessed via [http://localhost:8080](http://localhost:8080).

Note: this requires postgres now as some SQL statements are no longer supported by postgres.

## Packaging Application

To package the application, run the following:

```
./mvnw package
```