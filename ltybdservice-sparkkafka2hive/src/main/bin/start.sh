#!/bin/bash

# Get home directory
APP_HOME=$(cd $(dirname $0); cd ..; pwd -P)
echo APP_HOME is $APP_HOME

# Project Information
ARTIFACT_ID=ltybdservice-sparkkafka2hive
VERSION=0.0.1-SNAPSHOT
MAIN_JAR=$ARTIFACT_ID-$VERSION.jar
MAIN_CLASS=com.ltybdservice.Kafka2HiveApplication

spark-submit \
--verbose \
--class $MAIN_CLASS \
--master yarn \
--executor-memory 2G \
--num-executors 10 \
--jars  $APP_HOME/lib/*.jar \
$APP_HOME/lib/$MAIN_JAR
