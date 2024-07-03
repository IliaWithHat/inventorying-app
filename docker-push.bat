@echo off

set DOCKERHUB_USERNAME=iliawithhat

echo.
echo Building jar file
call ./gradlew.bat bootJar

echo.
echo Building Docker image
docker build -t inventorying-app-image .

docker tag inventorying-app-image:latest %DOCKERHUB_USERNAME%/inventorying-app:latest

echo.
echo Pushing image to Docker Hub
docker push %DOCKERHUB_USERNAME%/inventorying-app:latest

echo.
echo Image pushed to Docker Hub successfully.