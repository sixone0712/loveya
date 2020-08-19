#!/bin/sh

echo
echo START: postgresql_access.sh
echo

CUR=`dirname ${0}`

\cp ${CUR}/../data/postgres/pg_hba.conf /var/lib/pgsql/data/
\cp ${CUR}/../data/postgres/postgresql.conf /var/lib/pgsql/data/

# POSTGRES PORT OPEN
# (If you want to connect to DB from the outside)
#iptables -I INPUT 1 -p tcp --dport 5432 -j ACCEPT

# SAVE
#service iptables save

# RESTART
#service iptables restart

#systemctl restart postgresql
