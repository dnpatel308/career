#FROM openjdk:8u92-jre-alpine
FROM openjdk:8-jre

MAINTAINER Kushal Mehta <kushal.mehta@brodos.net>
EXPOSE 8181

ARG ASSEMBLY

# install needed system modules
#RUN apk add --update bash && \
#    apk add --update curl && \
#    apk add --update jq && \
#    rm -rf /var/cache/apk/*

COPY ./assembly/target/${ASSEMBLY}.tar.gz /srv

WORKDIR /srv
RUN tar xzf ${ASSEMBLY}.tar.gz && \
    rm ${ASSEMBLY}.tar.gz && \
    ln -s ${ASSEMBLY} karaf
    #sed -i '/security.provider.10/a security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider' /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/java.security
    #rm /usr/lib/jvm/java-1.8-openjdk/jre/lib/amd64/libmlib_image.so && \
    #rm /usr/lib/jvm/java-1.8-openjdk/jre/lib/amd64/libawt* && \
    #rm /usr/lib/jvm/java-1.8-openjdk/jre/lib/amd64/libfontmanager.so && \
    #rm /usr/lib/jvm/java-1.8-openjdk/jre/lib/amd64/libjsound* && \
    #rm /usr/lib/jvm/java-1.8-openjdk/jre/lib/amd64/libsplashscreen.so && \
    #rm /usr/lib/jvm/java-1.8-openjdk/jre/lib/amd64/libhprof.so && \
    #rm /usr/lib/jvm/java-1.8-openjdk/jre/lib/ext/nashorn.jar

#COPY appdynamics-agent/ /srv/karaf/appdynamics-agent
CMD /srv/karaf/bin/context-start run
