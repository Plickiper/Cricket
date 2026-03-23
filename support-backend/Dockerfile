FROM openjdk:17-oracle

LABEL maintainer="design.kurtjoseph@gmail.com"

WORKDIR /opt/app

EXPOSE 8080

COPY target/support-backend-1.0.0.jar /opt/app/support-backend.jar

ENTRYPOINT ["java", "-jar", "support-backend.jar"]