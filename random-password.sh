#! /bin/bash

cd $(dirname "$0")

DB_PASSWORD=$1;
DB_ROOT_PASSWORD=$2;
REDIS_PASSWORD=$3;

if [ -z "${DB_PASSWORD}" -a -z "${DB_ROOT_PASSWORD}" -a -z "${REDIS_PASSWORD}" ]
then
  mvn versions:set-property -Dproperty=db-password -DnewVersion=$(openssl rand -base64 16) -DgenerateBackupPoms=false;
  mvn versions:set-property -Dproperty=db-root-password -DnewVersion=$(openssl rand -base64 16) -DgenerateBackupPoms=false;
  mvn versions:set-property -Dproperty=redis-password -DnewVersion=$(openssl rand -base64 16) -DgenerateBackupPoms=false;
  echo "Project initialized passwords."
fi