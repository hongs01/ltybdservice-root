#!/bin/bash

# Get home directory
APP_HOME=$(cd $(dirname $0); cd ..; pwd -P)
echo APP_HOME is $APP_HOME

# Project Information
ARTIFACT_ID=ltybdservice-dataacquisition
VERSION=0.0.1-SNAPSHOT
MAIN_JAR=$ARTIFACT_ID-$VERSION.jar
MAIN_CLASS=com.ltybdservice.DataApplication

nohup /usr/hdp/current/spark2-client/bin/spark-submit \
--verbose \
--class $MAIN_CLASS \
--master yarn \
--executor-memory 1G \
--num-executors 2 \
--jars /usr/hdp/current/hbase-client/lib/hbase-protocol-1.1.2.2.6.0.3-8.jar,$APP_HOME/lib/fastjson-1.2.34.jar,$APP_HOME/lib/spark-sql-kafka-0-10_2.11-2.1.0.2.6.0.3-8.jar,$APP_HOME/lib/kafka-clients-0.10.0.1.jar,$APP_HOME/lib/hbase-common-1.1.2.2.6.0.3-8.jar,$APP_HOME/lib/hbase-client-1.1.2.2.6.0.3-8.jar \
$APP_HOME/lib/$MAIN_JAR &
