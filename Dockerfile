FROM openjdk:8u111-jdk-alpine
ADD /target/*-exec.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]