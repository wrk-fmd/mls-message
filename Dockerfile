# This Dockerfile builds and runs a Spring Boot application from a multi-module maven project
# It takes two build arguments:
# - MODULE is the path to the Spring Boot module, relative to the base pom file (e.g. module1/submodule2)
# - APP is the name of the generated Spring Boot jar file, normally the artifact id of the maven module

# First stage: Build the source code with maven
FROM maven:slim AS build
WORKDIR /app

# Copy all POM files
# All modules need to be included, even if they are not required for the build
# The list can be generated by running this from the directory with the base pom file:
# find -name pom.xml -printf "COPY %p %h/\n" | sort -t" " -k3 | column -t
COPY  ./pom.xml                    ./
COPY  ./message-api/pom.xml        ./message-api/
COPY  ./message-common/pom.xml     ./message-common/
COPY  ./message-fleetsync/pom.xml  ./message-fleetsync/
COPY  ./message-sms/pom.xml        ./message-sms/
COPY  ./message-tetra/pom.xml      ./message-tetra/

# Copy maven proxy config
COPY ./mvn-proxy.xml ./

# Resolve all dependencies, don't fail on missing (internal) dependencies
RUN mvn -s mvn-proxy.xml -B dependency:go-offline > /dev/null

# Specify the required module
ARG MODULE

# Copy full source code (generated code is ignored through .dockerignore)
COPY ./ ./

# Build the requested module and dependencies
RUN mvn -s mvn-proxy.xml -q package -pl ${MODULE} -am

# Second stage: Build the server image (needs only JRE)
FROM openjdk:jre-slim AS runtime

# Run as non-root
# TODO Make this work with mounted volumes
#RUN adduser --system --group spring
#USER spring

# Volume for Spring Boot to write to
VOLUME /tmp

# Volumes where the config and log directories are mounted
VOLUME /config
VOLUME /log

# The arguments have to be declared again
ARG MODULE
ARG APP
ARG INSTALL

# TODO This is kind of an ugly hack...
RUN if [ -n "$INSTALL" ]; then apt-get update && apt-get install $INSTALL; fi

# Run the Spring Boot application
COPY --from=build /app/${MODULE}/target/${APP}-*.jar /app/app.jar
ENTRYPOINT ["java", "-Dspring.config.location=file:/config/", "-jar", "/app/app.jar"]
