#!/bin/sh

function stop_services()
{
    systemctl stop httpd
    RET=$?
    if [ ${RET} -ne 0 ]
    then
        echo
        echo "Error: httpd stop error ! ret: ${RET}"
        echo
        exit 1
    fi

    echo "STATUS : stop tomcat service..."
    systemctl stop tomcat
    RET=$?
    if [ ${RET} -ne 0 ]
    then
        echo
        echo "Error: tomcat stop error ! ret: ${RET}"
        echo
        exit 1
    fi
    echo "STATUS : stop tomcat service successfully."

    echo "STATUS : stop tomcat9 service..."
    systemctl stop tomcat9
    RET=$?
    if [ ${RET} -ne 0 ]
    then
        echo
        echo "Error: tomcat9 stop error ! ret: ${RET}"
        echo
    else
        echo "STATUS : stop tomcat9 service successfully."
    fi
}

echo
echo "START: setup_ESP.sh"
echo

CUR=`dirname ${0}`

user=`whoami`
if [ ${user} != "root" ]
then
    echo
    echo "Error: Execution user must be root!"
    echo
    exit 1
fi

test -d /usr/local/canon/ots
RET=$?
if [ ${RET} -eq 0 ]
then
    echo "This System is OTS."
    echo "Cannot install RSS ESP package."
    exit 1
fi

test -d /usr/local/canon/esp
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "This System is not ESP."
    echo "Cannot install RSS ESP package."
    exit 1
fi

stop_services

CUR=`dirname ${0}`

sh "${CUR}/scripts/tomcat9_init.sh"
sh "${CUR}/scripts/tomcat9_esp_init.sh"

sh "${CUR}/scripts/logrotate_init.sh"

sh "${CUR}/scripts/httpd_esp_init.sh"
sh "${CUR}/scripts/service_ctrl_esp_init.sh"

# initalize postgres
sh "${CUR}/scripts/postgresql_init.sh"

# access permission postgres
sh "${CUR}/scripts/postgresql_access.sh"

\cp -f "${CUR}/data/ELogCollector.jar" /usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar
rm -rf /usr/local/tomcat9/apache-tomcat-9.0.35/webapps/ROOT
\cp -f "${CUR}/data/rssportal.war" /usr/local/tomcat9/apache-tomcat-9.0.35/webapps/rssportal.war
\cp -f "${CUR}/data/fsm.war" /usr/local/tomcat9/apache-tomcat-9.0.35/webapps/fsm.war
\cp -f "${CUR}/data/fsc.war" /usr/local/tomcat9/apache-tomcat-9.0.35/webapps/fsc.war

chown eespuser:eespuser /usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar
mkdir -p /LOG/downloads
mkdir -p /LOG/wdownloads
rm -rf /LOG/rssportal
rm -rf /LOG/autocollect
rm -rf /LOG/cache
rm -rf /LOG/zip
mkdir -p /LOG/rssportal
mkdir -p /LOG/autocollect
mkdir -p /LOG/cache
mkdir -p /LOG/zip
chown -R tomcat:tomcat /LOG/downloads
chown -R tomcat:tomcat /LOG/wdownloads
chown -R tomcat:tomcat /LOG/rssportal
chown -R tomcat:tomcat /LOG/autocollect
chown -R tomcat:tomcat /LOG/cache
chown -R tomcat:tomcat /LOG/zip

echo
echo "END: setup_ESP.sh"
echo