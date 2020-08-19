#!/bin/sh

echo
echo START: service_ctrl_init.sh
echo

CUR=`dirname ${0}`

\cp -f ${CUR}/../data/ots/service_ctrl.sh /usr/local/canon/ots/canon/ees/tools/Startup/

