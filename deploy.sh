#!/usr/bin/env bash

BUILD_PATH=$(ls /home/ec2-user/build/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_PATH)
echo "> build 파일명: $JAR_NAME  $BUILD_PATH"

echo "> build 파일 복사"
DEPLOY_PATH=/home/ec2-user/
cp $BUILD_PATH $DEPLOY_PATH

echo "> springboot-deploy.jar 교체"
CP_JAR_PATH=$DEPLOY_PATH$JAR_NAME
APPLICATION_JAR_NAME=springboot-deploy.jar
APPLICATION_JAR=$DEPLOY_PATH$APPLICATION_JAR_NAME

echo "> $CP_JAR_PATH       > $APPLICATION_JAR"

ln -Tfs $CP_JAR_PATH $APPLICATION_JAR

echo "> 현재 실행 중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -f $APPLICATION_JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  ehco "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $APPLICATION_JAR_NAME 배포"
nohup java -jar \
  -Dspring.config.location=classpath:/config/application-prod.yml \
  $APPLICATION_JAR > /dev/null 2> /dev/null < /dev/null &