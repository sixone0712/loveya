#!/bin/sh

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
    sh ./setup_OTS.sh
    RET=$?
    exit ${RET}
fi

test -d /usr/local/canon/esp
RET=$?
if [ ${RET} -eq 0 ]
then
    echo "This System is ESP."
    sh ./setup_ESP.sh
    RET=$?
    exit ${RET}
fi

echo "This System is neither ESP nor OTS."
echo "Cannot install RSS package."
