FROM eclipse-temurin:21-jre
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} inventorying-app.jar
ENTRYPOINT ["java","-jar","inventorying-app.jar"]