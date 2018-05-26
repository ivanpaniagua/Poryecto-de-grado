@echo off
rem EN JBUILDER C:\Apache\jbproject\Business\SRC\mysql\mysql.bat parametro $FILEDIR
rem -----Para la Base de Datos en Mysql----------------------------------------
rem 2003 ANTES ERA "c:\archivos de programa\mysql\bin\mysql" -u root < bd.sql

rem "c:\archivos de programa\mysql\bin\mysqld-opt.exe"
rem cd %1
"C:\Program Files (x86)\MySQL\MySQL Server 5.5\bin\mysql" -u root -p mio < bd.sql

echo Bien Ok.

