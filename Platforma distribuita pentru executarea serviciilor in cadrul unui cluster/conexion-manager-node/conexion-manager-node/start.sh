#!/bin/bash
clear
PORT=8080

while true; do
    if ! nc -z localhost $PORT; then
        echo "Portul $PORT este disponibil."
        break
    fi
    PORT=$((PORT+1))
done

/usr/lib/jvm/java-17-openjdk-amd64/bin/java \
-Dcatalina.home=/opt/tomcat9/apache-tomcat-9.0.8 \
-Dcatalina.base=/root/.SmartTomcat/conexion-manager-node/conexion-manager-node \
-Djava.io.tmpdir=/root/.SmartTomcat/conexion-manager-node/conexion-manager-node/temp \
-Djava.util.logging.config.file=/root/.SmartTomcat/conexion-manager-node/conexion-manager-node/conf/logging.properties \
-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager \
-javaagent:/home/madab/ideaIC-2023.3.3/idea-IC-233.14015.106/lib/idea_rt.jar=35135:/home/madab/ideaIC-2023.3.3/idea-IC-233.14015.106/bin \
-Dfile.encoding=UTF-8 \
-classpath /opt/tomcat9/apache-tomcat-9.0.8/bin/bootstrap.jar:/opt/tomcat9/apache-tomcat-9.0.8/bin/tomcat-juli.jar \
-Dserver.port=$PORT \
org.apache.catalina.startup.Bootstrap start
