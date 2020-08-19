#!/bin/sh

# COPY server.xml
CUR=`dirname ${0}`

\cp -f ${CUR}/../data/esp/server.xml /usr/local/tomcat9/apache-tomcat-9.0.35/conf/server.xml
chown tomcat:tomcat /usr/local/tomcat9/apache-tomcat-9.0.35/conf/server.xml