#!/bin/bash

# Get home directory
APP_HOME=$(cd $(dirname $0); cd ..; pwd -P)
echo APP_HOME is $APP_HOME

# Project Information
ARTIFACT_ID=ltybdservice-kafka2hive
VERSION=0.0.1-SNAPSHOT
MAIN_CLASS=com.ltybdservice.Kafka2HiveApplication

# Set entry
MAIN_JAR_DIR=lib
MAIN_JAR=$ARTIFACT_ID-$VERSION.jar

# Set log directory for log4j
LOG_DIR=$APP_HOME/logs
mkdir -p $LOG_DIR

# Set Java environment
JAVA_ENV="-Dlog-dir=$LOG_DIR"

# Run
if [ $# != 1 ] ; then
echo "USAGE: $0 topologyName"
echo " e.g.: $0 Kafka2Hive"
exit 1;
fi
storm jar  $APP_HOME/$MAIN_JAR_DIR/$MAIN_JAR $MAIN_CLASS $1
