#!/bin/bash

# Get home directory
APP_HOME=$(cd $(dirname $0); cd ..; pwd -P)
echo APP_HOME is $APP_HOME

# Project Information
ARTIFACT_ID=ltybdservice-persononbus
VERSION=0.0.1-SNAPSHOT
MAIN_JAR=$ARTIFACT_ID-$VERSION.jar
MAIN_CLASS=com.ltybdservice.PersonOnBusApplication

spark-submit \
--verbose \
--class $MAIN_CLASS \
--master yarn \
--executor-memory 1G \
--num-executors 2 \
--jars  $APP_HOME/lib/mysql-connector-java-5.1.6.jar,$APP_HOME/lib/ltybdservice-utils-0.0.1-SNAPSHOT.jar,$APP_HOME/lib/snakeyaml-1.19.jar \
$APP_HOME/lib/$MAIN_JAR
