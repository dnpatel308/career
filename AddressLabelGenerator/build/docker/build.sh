#!/bin/bash
scriptname=context-start

. ../assembly/target/classes/project.properties
# prepare environment file for docker-compose
cp ../assembly/target/classes/project.properties .env
echo >> .env
echo "scriptname=${scriptname}" >> .env
# image names have to be all-lowercase
echo "imageName=${contextName,,}" >> .env
# copy assembly in place
cp ../assembly/target/${finalName}.tar.gz .
# prepare empty 'appdynamics-agent' directory if not already existing
# to satisfy docker build step
[ ! -d "appdynamics-agent" ] && mkdir appdynamics-agent
docker-compose build
# cleanup
rm ${finalName}.tar.gz
