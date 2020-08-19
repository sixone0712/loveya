#!/bin/sh

echo
echo START: service_ctrl_init.sh
echo

CUR=`dirname ${0}`

\cp -f ${CUR}/../data/esp/service_ctrl.sh /usr/local/canon/esp/tools/Startup/

