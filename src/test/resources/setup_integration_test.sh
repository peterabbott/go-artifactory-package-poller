#!/bin/sh

[ -z "$1" ] && exit 1

HOST_AND_PORT=$1
CREDS="-u admin:password"

count=0
while [ $(curl --silent  -w "%{http_code}" -o /dev/null http://$HOST_AND_PORT/artifactory/webapp/) -ne 200 ]; do
  echo "Attempt: $count"
  if [ $count -gt 10 ]; then echo "gave up waiting"; exit 100; fi
  count=$(($count + 1))
  sleep 10
done

set -e

echo "Creating Data"
# setup lib release
curl -o /dev/null --silent -w "%{http_code}" http://$HOST_AND_PORT/artifactory/libs-release/commons-io/commons-io/2.3/commons-io-2.3.jar
curl -o /dev/null --silent -w "%{http_code}" http://$HOST_AND_PORT/artifactory/libs-release/commons-io/commons-io/2.3/commons-io-2.3-sources.jar
curl -o /dev/null --silent -w "%{http_code}" http://$HOST_AND_PORT/artifactory/libs-release/commons-io/commons-io/2.4/commons-io-2.4.jar
curl -o /dev/null --silent -w "%{http_code}" http://$HOST_AND_PORT/artifactory/libs-release/commons-io/commons-io/2.4/commons-io-2.4-sources.jar

# recreate a snapshot scenario
curl ${CREDS} -XPUT --data "{\"path\": \"/ext-snapshot-local/a/\"}" http://$HOST_AND_PORT/artifactory/ext-snapshot-local/a/
curl ${CREDS} -XPUT --data "{\"path\": \"/ext-snapshot-local/a/groupId/\"}" http://$HOST_AND_PORT/artifactory/ext-snapshot-local/a/groupId/
curl ${CREDS} -XPUT --data "{\"path\": \"/ext-snapshot-local/a/groupId/artifactId/\"}" http://$HOST_AND_PORT/artifactory/ext-snapshot-local/a/groupId/artifactId/
curl ${CREDS} -XPUT --data "{\"path\": \"/ext-snapshot-local/a/groupId/artifactId/1.0-SNAPSHOT/\"}" http://$HOST_AND_PORT/artifactory/ext-snapshot-local/a/groupId/artifactId/1.0-SNAPSHOT/

for i in $(seq 1 3); do
    ID=$(date  +"%Y%m%d.%H%M-%s")
    curl ${CREDS} -XPUT --data "{\"path\": \"/ext-snapshot-local/a/groupId/artifactId/1.0-SNAPSHOT/artifactId-1.0-${ID}-$i.jar\"}" http://$HOST_AND_PORT/artifactory/ext-snapshot-local/a/groupId/artifactId/1.0-SNAPSHOT/artifactId-1.0-${ID}-$i.jar
done
