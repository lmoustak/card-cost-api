# Card Cost API

This project contains the source code for the Card Cost API assignment.

## Building the project

Run the following command in the root folder:

```sh
mvn clean package -DskipTests
```

Or:

```sh
mvnw clean package -DskipTests
```

This will produce the .JAR file to run the application

## Running the project as a Docker container

Next, run

```sh
docker compose up
```

to build and initiate the container along a PostgreSQL and Redis container.

Note the JAR file must be in the ./target folder by default.

## Accessing the OpenAPI documentation

The api-docs.json file contains the JSON schema for the API. You can also view it under http://localhost:8080/swagger-ui.html, after starting Docker.
