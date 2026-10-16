# gestion-personnes

A Java and Spring Boot learning project providing a REST API for person records.
It demonstrates layered application design, persistence with Spring Data JPA,
request validation, pagination, filtering, and automated tests.

Source identifiers and application messages remain in French. This documentation
is in English to make the project easier to review.

## Technologies Used

- Java 25
- Spring Boot 4.1.0 and Spring Web MVC
- Spring Data JPA and H2
- Jakarta Validation
- Maven; the included Maven Wrapper selects Maven 3.9.16
- JUnit Jupiter, Mockito, Spring Boot Test, and MockMvc

`pom.xml` defines the application dependencies. The wrapper version is configured
in `.mvn/wrapper/maven-wrapper.properties`.

## Features

- Create, retrieve, update, and delete person records.
- List records with pagination and sorting.
- Combine optional filters: partial name (case-insensitive), person type, active
  status, minimum age, and maximum age.
- Apply inclusive age bounds; reject non-positive or reversed search bounds.
- Activate or deactivate a person through a dedicated endpoint.
- Preserve the existing active status when updating personal details.
- Validate required names, email format, positive age, and person type.
- Reject creation when the email already exists.
- Return HTTP 404 for unknown IDs and HTTP 409 for duplicate-email creation.

## Project Architecture

```text
src/main/java/com/example/personnes/
  controller/  HTTP endpoints
  service/     Business rules and search specifications
  repository/  Spring Data JPA repository
  model/       Person entity, builder, and person-type enum
  dto/         Request and response records
  factory/     Entity creation from request data
  exception/   Application exceptions and centralized error handling
src/main/resources/  Application and development configuration
src/test/java/       Service, controller, repository, and integration tests
src/test/resources/  In-memory database and Mockito test configuration
```

Requests flow from controller to service to repository. DTOs define the public
payloads. JPA specifications build the optional search criteria.

## Getting Started

### Prerequisites

- A JDK 25 installation, with `JAVA_HOME` pointing to it.
- Internet access for the first Maven Wrapper run and dependency downloads.
- A terminal; `curl` is useful for the examples below.

### Installation

Obtain a copy of this repository and open its root directory, containing `pom.xml`
and `mvnw`. No separate Maven installation is required.

```bash
java -version
./mvnw --version
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

### Running the Application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.arguments=--server.address=127.0.0.1
```

The API is available at `http://127.0.0.1:8080/api/personnes`. The development
profile persists H2 data under `./data/` and updates the schema through Hibernate.
This local directory is ignored by Git. Use fictional records for demonstrations.

The development profile also enables the H2 console at
`http://127.0.0.1:8080/h2-console`, using JDBC URL
`jdbc:h2:file:./data/personnesdevdb`, username `sa`, and an empty password.
These settings are intended for local learning only.

### Running Tests

```bash
./mvnw test
```

Tests cover the service, HTTP controller, JPA repository, and application
integration. Database tests use an in-memory H2 database through the `test`
profile, separate from the development database. A regression test checks that
updating a disabled person does not reactivate that person.

### Packaging

```bash
./mvnw package
java -jar target/gestion-personnes-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.address=127.0.0.1
```

## Example Usage

Run the following examples against an empty development database. Replace `1`
with the ID returned by the create request if necessary.

```bash
# Create a person (returns HTTP 200 and a PersonneResponse)
curl -i -X POST http://127.0.0.1:8080/api/personnes \
  -H 'Content-Type: application/json' \
  -d '{"nom":"Martin","prenom":"Alex","email":"alex@example.com","age":25,"type":"ETUDIANT"}'

# Retrieve a person
curl -i http://127.0.0.1:8080/api/personnes/1

# Search and paginate; page numbers start at zero
curl -i 'http://127.0.0.1:8080/api/personnes?nom=mart&type=ETUDIANT&actif=true&ageMin=20&ageMax=30&page=0&size=5&sort=nom,asc'

# Deactivate a person
curl -i -X PATCH http://127.0.0.1:8080/api/personnes/1/actif \
  -H 'Content-Type: application/json' -d '{"actif":false}'

# Update details, keeping the same email; active status remains unchanged
curl -i -X PUT http://127.0.0.1:8080/api/personnes \
  -H 'Content-Type: application/json' \
  -d '{"nom":"Martin","prenom":"Alex","email":"alex@example.com","age":26,"type":"EMPLOYE"}'

# Delete a person (returns HTTP 200 with an empty body)
curl -i -X DELETE http://127.0.0.1:8080/api/personnes/1
```

The allowed person types are `ETUDIANT`, `EMPLOYE`, and `AUTRE`. Creation sets
`actif` to `true`. List responses include `content` and pagination metadata.

## Development Process and AI Contributions

This learning project was developed by Hendrix Jordan Keyanfe Diffo:

- **Personal baseline (`5dc2fcc`):** CRUD API, pagination, JPA persistence,
  DTO validation, error handling, and initial tests, with AI used as a learning aid.
- **AI-assisted extensions:** active-status management (`e203b70`), multi-criteria
  search (`9fa9850`), and age filtering (`237a702`), plus test-configuration support.
- **AI-assisted portfolio preparation:** the status-preservation fix and regression
  test, fictional test emails, local-file exclusions, and this documentation.

This attribution reflects the author's confirmed workflow. The existing Git
history is preserved; later AI extensions are not claimed as independent work.

## Current Limitations

- This is a local learning application, with no authentication or authorization.
  It is not intended to store real personal data or to be exposed publicly as a
  running service.
- Updates identify a person by email, not by a path ID. Changing the email is not
  supported. An unknown email during an update currently returns HTTP 400.
- Email uniqueness is checked in application code during creation; there is no
  database uniqueness constraint to protect against concurrent duplicate requests.
- Name filters use SQL `LIKE` semantics: `%` and `_` may act as wildcards.
- H2 and automatic schema updates are development choices; no production database
  migration process is included.
- Tests cover selected scenarios, not every edge case or production requirement.
- Previous commits retain their original test fixtures. Replacing current test
  emails does not remove the old addresses from Git history.
