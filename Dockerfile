#####################################################################################
########################## Stage 1: Docker Builder Image ############################
#####################################################################################
FROM amazoncorretto:17.0.7-al2023 AS builder
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline &&  \
    ./mvnw clean package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

#####################################################################################
######################## Stage 2: Docker Application Image ##########################
#####################################################################################
FROM amazoncorretto:17.0.7-al2023-headless
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib     /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF         /app/META-INF
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.hiperium.city.tasks.api.TasksApplication"]
