@echo off

echo.
echo Building jar file
call ./gradlew.bat bootJar

echo.
echo Starting docker containers...
docker-compose up -d