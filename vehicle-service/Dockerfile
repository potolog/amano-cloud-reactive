FROM openjdk:8u111-jdk-alpine

ADD target/*.jar app.jar

EXPOSE 9020
ENTRYPOINT ["java","-Xmx400M","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]