#!/bin/sh

CUR=`dirname ${0}`

echo
echo START: httpd_init.sh
echo

HOSTNAME=`hostname -I`

echo "HOSTNAME : $HOSTNAME"

echo "
<Location /fsc>
    ProxyPass ajp://localhost:8020/fsc
    
    Order deny,allow
    Deny from all
    Allow from 127.0.0.1
    Allow from ${HOSTNAME}
    Allow From ALL

</Location>" > /etc/httpd/conf.d/fsc.conf