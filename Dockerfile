
FROM openjdk:8-jdk-alpine
MAINTAINER Krzysztof Karski <krzysztof.karski@emergenttech.com>

ENV REPO_DEFS=classpath:repo-defaults.yml
ENV PORT=8891

COPY ./target/appconfig-service-*.jar /apps/
WORKDIR /apps
RUN mv ./appconfig-service-2.0.0.jar ./appconfig-service.jar

EXPOSE $PORT
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Drepo=$REPO_DEFS -jar ./appconfig-service.jar $PORT
