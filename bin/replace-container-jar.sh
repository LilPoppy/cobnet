#! /bin/bash

cd $(dirname $(dirname "$0"))

APP_ID=$1;

if [ -z "${APP_ID}" ]
then
APP_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout);
fi

docker cp target/${APP_ID}.jar ${APP_ID}:/cobnet/target/${APP_ID}.jar