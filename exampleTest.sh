
docker-compose build
#start docker in detached mode
docker-compose up -d wildfly10
#wait for docker to be running
echo "waiting for wildfly10"
.docker/wait-for-it.sh localhost:8080 -t 0  -- echo "wildfly10 is up"



docker exec -i $(docker-compose ps -q wildfly10) sh -c "cd /data/ && export JBOSS_HOME_TEST=/opt/jboss/wildfly/ && mvn verify  -Dcheckstyle.skip=true -Dgpg.skip=true -B -V -Pquickrun,integration-test -rf :seam-integration-tests";


docker-compose down