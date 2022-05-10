cd $(dirname $(dirname "$0"))

echo "Make sure you have configured pom.xml for 'db-host=db' and 'redis-host=redis'"

APP_ID=$1;

if [ -z "${APP_ID}" ]
then
APP_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout);
fi

mvn clean package;
docker cp target/${APP_ID}.jar ${APP_ID}:/cobnet/target/${APP_ID}.jar
docker stop ${APP_ID}
docker start -a ${APP_ID}