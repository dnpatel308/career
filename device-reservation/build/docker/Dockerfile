#FROM openjdk:10-jdk-slim
FROM adoptopenjdk/openjdk8


LABEL MAINTAINER="Kushal Mehta <kushal.mehta@brodos.net>"
EXPOSE 8080

ARG VERSION

COPY build/docker/context-start /opt/context-start
# install needed system modules
RUN apt-get update \
    && apt-get install -y --allow-unauthenticated bash \
                                                  curl \
                                                  jq \
                                                  netcat \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir /etc/device-reservation    \
    && chmod 777 /opt/context-start

COPY ./target/device-reservation-*.jar /opt/.
COPY ./target/classes/application.properties /opt/.
COPY ./target/classes/brodos.mail.properties /opt/.
COPY ./target/classes/brodos.ticket.properties /opt/.
COPY ./target/classes/device.context.properties /opt/.
COPY ./log4j2.properties /opt/log4j2.properties
COPY ./data/ /opt/data/

CMD /opt/context-start
