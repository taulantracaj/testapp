#!/bin/sh
TIMEOUT=30
echo Deploying version $VERSION ...

echo Waiting for old version to stop ...
curl -m $TIMEOUT -o /dev/null http://localhost:9099
COUNTER=0
until [ $(ps | grep java | wc -l) -eq 0 ]; do
    echo Waiting ...
    sleep 1
    let COUNTER=COUNTER+1
    if [ $COUNTER -gt $TIMEOUT ]; then
        echo Error: instance failed to shutdown within $TIMEOUT seconds. Killing ...
        killall java
    fi
done

echo Removing old version ...
rm cdzd-app-*.jar

echo Installing new version ...
curl -o cdzd-app-$VERSION.jar http://10.0.2.2:8081/artifactory/libs-release/com/axelfontaine/cdzd-app/$VERSION/cdzd-app-$VERSION.jar
nohup java -jar cdzd-app-$VERSION.jar 0<&- &>log.txt &

echo Waiting for new version to start ...
COUNTER=0
until [ $( curl -w %{http_code} -s -o /dev/null http://localhost:9100) -eq 200 ]; do
    if [ $COUNTER -gt $TIMEOUT ]; then
        echo Error: instance failed to come up within $TIMEOUT seconds with HTTP 200 !
        exit 1
    fi
    echo Waiting ...
    sleep 1
    let COUNTER=COUNTER+1
done

echo Done.
