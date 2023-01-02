FROM openjdk:17-alpine as WebApp
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENV H2_USERNAME="user"
ENV H2_PASSWORD="herring"
ENV H2_FOLDER=faircorp
ARG JAR_FILE=build/libs/\*OT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]