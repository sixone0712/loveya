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


CUR=`dirname ${0}`

echo
echo "START: setup_OTS.sh"
echo

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
if [ ${RET} -ne 0 ]
then
    echo "This System is not OTS."
    echo "Cannot install RSS OTS package."
    exit 1
fi

stop_services

sh "${CUR}/scripts/tomcat9_init.sh"
sh "${CUR}/scripts/tomcat9_ots_init.sh"

sh "${CUR}/scripts/logrotate_init.sh"

sh "${CUR}/scripts/httpd_ots_init.sh"

sh "${CUR}/scripts/service_ctrl_ots_init.sh"

\cp -f "${CUR}/data/ELogCollector.jar" /usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar
rm -rf /usr/local/tomcat9/apache-tomcat-9.0.35/webapps/ROOT
\cp -f "${CUR}/data/fsc.war" /usr/local/tomcat9/apache-tomcat-9.0.35/webapps/fsc.war

chown eespuser:eespuser /usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar
chown -R tomcat:tomcat /LOG/downloads
chown -R tomcat:tomcat /LOG/wdownloads

echo
echo "END: setup_OTS.sh"
echo