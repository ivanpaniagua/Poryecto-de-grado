@echo off

rem   en JBUILDER C:\Apache\jbproject\Business\SRC\postgresql\postgresql.bat sin parametros



rem   NOW(2003).- psql -f /cygdrive/d/apache/src/Business/SRC/postgresql/bd.sql -h localhost mio


rem   primeramente añadir la variable de entorno C:\cygwin\bin
rem   luego ipc-daemon& sino no correra el siguiente comando
rem   $: initdb -W -D /usr/local/pgsql/data
rem   solo la primera vez si falla entonces borra ese directorio
rem   createdb -h localhost mio
rem   psql -h localhost mio

bash --login -c ipc-daemon&
rm /usr/local/pgsql/data/postmaster.pid
bash --login -i pg_ctl start -o -i -D /usr/local/pgsql/data -l logdb.log

rem   se puede añadir al final el parametro : -m fast
rem   bash --login -i pg_ctl stop -D /usr/local/pgsql/data
rem   dropdb -h localhost mio  luego createdb -h localhost mio
rem   luego psql -f bd.sql -h localhost mio
rem   COPY t_dpf FROM '/cygdrive/e/Program\ Files/mysql/bin/libro.txt';

echo Bien Ok.

rem   se puede deshabilitar la opcion de sync inicializando el postmaster con la opcion de -o -F
rem   se puede monitorear la actvidad del sistema con SELECT * FROM pg_stat_activity

rem   se ha eliminado de pg_hba.conf
rem   local      all                                          trust
rem   host       all         127.0.0.1     255.255.255.255    trust
rem   y se añadido
rem   host       mio         127.0.0.1     255.255.255.255    password

rem   # MIO  NO SE TOMA EN CUENTA LA SEGUNDA
rem   # host       all         127.0.0.1     255.255.255.255    trust
rem   # host       mio         127.0.0.1     255.255.255.255    password
rem   #
rem   # TYPE     DATABASE    IP_ADDRESS    MASK               AUTH_TYPE  AUTH_ARGUMENT
rem   host       all         127.0.0.1     255.255.255.255    trust
rem   host       all         127.0.0.1     255.255.255.255    md5   Administrator
