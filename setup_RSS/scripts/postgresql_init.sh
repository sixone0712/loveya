#!/bin/sh

echo
echo START: postgresql_init.sh
echo

CUR=`dirname ${0}`

postgresql-setup initdb
systemctl start postgresql
systemctl enable postgresql 

cd ~postgres/

systemctl restart postgresql 

su -c "psql -c \"CREATE ROLE rssadmin WITH LOGIN PASSWORD '1234' \"" postgres 
su -c "psql -c \"alter user rssadmin with superuser \"" postgres
su -c "psql -c \"alter user rssadmin with createdb \"" postgres
su -c "psql -c \"alter user rssadmin with createrole \"" postgres
su -c "psql -c \"alter user rssadmin with replication \"" postgres
su -c "psql -c \"DROP DATABASE rssdb; \"" postgres
su -c "psql -c \"CREATE DATABASE rssdb OWNER rssadmin \"" postgres 

systemctl restart postgresql 

