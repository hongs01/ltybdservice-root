#!/bin/bash

# Get root directory
APP_HOME=$(cd $(dirname $0); pwd -P)

echo APP_HOME is $APP_HOME

#clean  root

call mvn clean -Dmaven.test.skip=true
#compile scala file
cd %APP_HOME%/ltybdservice-schedul/
call mvn clean scala:compile compile -Dmaven.test.skip=true

cd ..
call mvn package -Dmaven.test.skip=true
echo install over
