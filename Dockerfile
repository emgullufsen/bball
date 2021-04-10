FROM openjdk:8-alpine

COPY target/uberjar/bball.jar /bball/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/bball/app.jar"]
