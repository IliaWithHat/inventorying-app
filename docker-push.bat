@echo off

set DOCKERHUB_USERNAME=iliawithhat
set APP_NAME=inventorying-app

echo.
echo Building jar file
call ./gradlew.bat bootJar

echo.
echo Building Docker image
docker build -t %APP_NAME% .

echo.
echo Setting tag to image
docker tag %APP_NAME%:latest %DOCKERHUB_USERNAME%/%APP_NAME%:latest

echo.
echo Pushing image to Docker Hub
docker push %DOCKERHUB_USERNAME%/%APP_NAME%:latest

echo.
echo Removing local image
docker rmi %APP_NAME%

echo.
echo Image pushed to Docker Hub successfully.