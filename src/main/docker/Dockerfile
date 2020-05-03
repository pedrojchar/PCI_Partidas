#
# ---- Base Node ----
FROM alpine:edge AS base
RUN apk add --no-cache openjdk8

# Set working directory
WORKDIR /usr/src/api

ADD api-java-template-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
