#!/usr/bin/env bash
mkdir -p ../logs/
pid=`jps | grep AppMain | awk '{print $1}'`
if [ "$pid" != "" ]
then
    echo "kill $pid"
    kill $pid
fi
sleep 2
JVM_ARGS="-Xms256m -Xmx256m -Xloggc:$HOME/painter/logs/gc/gc.log"
java $JVM_ARGS -cp ../conf:../lib/ar-common-1.0-SNAPSHOT.jar com.larkin.web.AppMain $@ 2>&1
