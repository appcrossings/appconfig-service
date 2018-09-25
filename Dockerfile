
FROM openjdk:8-jdk-alpine
MAINTAINER Krzysztof Karski <krzysztof.karski@emergenttech.com>

ENV REPO_DEFS=classpath:repo-defaults.yml
ENV PORT=8871

COPY ./target/appconfig-service-*-exec.jar /apps/
WORKDIR /apps
RUN mv ./appconfig-service-*-exec.jar ./appconfig-service.jar

EXPOSE $PORT
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Drepo=$REPO_DEFS -jar ./appconfig-service.jar $PORT
