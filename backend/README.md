# Parking Management App (Backend)

##### Language: Java 21
##### Framework: SpringBoot 3.2
##### Database: Postgres
##### Build with Gradle
##### Port: 8090

### How to run locally using Docker compose:
1. Install Docker compose
2. In backend directory - run: **docker-compose up**

### How to run locally (run database with Docker compose):
1. Install Java
2. Install Docker compose
3. In backend directory - run: **docker-compose up postgres**
4. In backend directory - run: **./gradlew build**
5. In backend directory - run: **./gradlew bootRun**

### How to run locally (without Docker):
1. Install Java 21
2. Install Postgres database
3. Configure and run Postgres database
4. Set **backend/src/main/resources/application.yml - spring.datasource.url:** (your database url)
5. Set **backend/src/main/resources/application.yml - spring.datasource.username:** (your database user)
6. Set **backend/src/main/resources/application.yml - spring.datasource.password:** (your database password)
7. Set **backend/src/main/resources/application.yml - spring.jpa.hibernate.ddl-auto: create**
8. In backend directory - run: **./gradlew build**
9. In backend directory - run: **./gradlew bootRun**

## Swagger: http://localhost:8090/swagger-ui/index.html#
Api secured with token JWT.