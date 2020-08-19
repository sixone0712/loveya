#!/bin/sh

CUR=`dirname ${0}`

echo
echo START: logrotate_init.sh
echo

\cp -f ${CUR}/../data/logrotate.d/tomcat9 /etc/logrotate.d/
