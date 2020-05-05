rapidpass-api
-------------

For details about this project please visit https://gitlab.com/dctx/rapidpass-api/-/wikis/About-this-project

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


**To setup Keycloak locally:**

1. Login to http://localhost:8180/auth `admin/admin`.
1. Import local realm. `Add Realm` -> `Import` -> Select file `keycloak/realm-export.json`

This sets up the following:
* `rapidpass-api-local` realm
* `rapidpass-api` client 
*  user `user/user` that has `approver` role.


**To authenticate locally:**
1. `POST` to keycloak login with user/password
```
curl --request POST \
  --url 'http://localhost:8180/auth/realms/rapidpass-api-local/protocol/openid-connect/token' \
  --header 'content-type: application/x-www-form-urlencoded' \
  --cookie 'JSESSIONID=39E998D029DD589579745FD970D8EC21.7819c6ae3fb3; JSESSIONID=6E9B165D5E3D44F6824BF5D419190D12' \
  --data client_id=rapidpass-api \
  --data username=user \
  --data password=user \
  --data grant_type=password \
  --data client_secret=22f20434-39cd-4e39-9620-99f99a6b0334 
```
1. extract token from response `access_token`
1. send token to endpoint as `Authorization Bearer` header
```
curl --request GET \
  --url http://localhost:8080/api/v1/registry/access-passes \
  --header 'authorization: Bearer {access_token}' 
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