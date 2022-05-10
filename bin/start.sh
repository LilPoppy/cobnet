#! /bin/bash

cd $(dirname $(dirname "$0"))

if [ "$#" == 0 ];
then
  ACTION=$(mvn help:evaluate -Dexpression=docker-target -q -DforceStdout);
  APP_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout);
elif [ "$#" == 1 ]
then
  ACTION=$1;
  APP_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout);
elif [ "$#" == 2 ]
then
  ACTION=$1;
  APP_ID=$2;
fi

if [ "${ACTION}" == "native" ]
then
  eval "./target/${APP_ID}";
elif [ "${ACTION}" == "jvm" ]
then
  JAVA_HOME=$JAVA_HOME;
  export JAVA_HOME;
  PATH=$JAVA_HOME/bin:$PATH;
  export PATH;
  which java;
  java -version;
  java -jar ./target/${APP_ID}.jar;
else
  echo "${ACTION} is not an available target.";
fi