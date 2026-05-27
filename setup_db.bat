@echo off
set PGPASSWORD=root
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE DATABASE spliteasedb;"
echo Done.
