#!/bin/bash
# */1 * * * * root /usr/local/qtl-cm-feedback/mon.sh > /dev/null 2>&1
processCnt=$(ps -ef | grep java | grep qtl-cm-feedback | wc -l)
metric=cm-feedback-error
tags=DataType=alarmData,Param=AliveError
if [ "$processCnt" != "1" ];then
    echo "qtl-cm-feedback alive error"
    curl -X POST -H "Content-Type:application/json; charset=utf-8" -d \
    "[{\"Metric\":\"$metric\",\"Tags\":\"$tags\",\"Value\":\"$processCnt\", \
    \"Timestamp\":$(date +%s),\"Endpoint\":\"$(hostname)\",\"CounterType\":\"GAUGE\",\"Step\":60}]" \
    http://127.0.0.1:2121/v1/push
else
    echo "normal"
fi