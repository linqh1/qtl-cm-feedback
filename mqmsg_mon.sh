#!/bin/bash
cd /home/rocketmq/rocketmq-all-4.6.0-bin-release/bin/

consumerGroup=$1
timeThreshold=$2
cntThreshold=$3
echo "######## Message Delay Monitor ($consumerGroup) ########"
echo "######## Alert Threshold: DelayTime>=$timeThreshold Or DelayCnt>=$cntThreshold ########"

progressDetail=$(./mqadmin consumerProgress -n 127.0.0.1:9876 -g "$consumerGroup")
echo "$progressDetail"

delayCnt=$(echo "$progressDetail" | grep "Diff Total" | tail -n 1 | sed 's/Diff Total: //g')
delaySec=0

progressDetail=$(echo "$progressDetail" | sed -n '/#Topic/,/^$/p' | tail -n +2)

if [ "$progressDetail" = "" ];then
    echo "error mq output"
    exit 1
fi

now=$(date +%s)
while read -r line
do
  consumerDate=$(echo "$line" | awk '{print $8,$9}' | awk 'gsub(/^ *| *$/,"")' )
  if [ "$consumerDate" != "N/A" ];then
    consumerDate=$(date +%s -d "$consumerDate")
    diff=$((now - consumerDate))
    echo "$timeThreshold $diff $delaySec"
    if (( diff > timeThreshold )) && (( diff > delaySec ));then
      delaySec=$diff
    fi
  fi
done< <(echo "$progressDetail" | awk '{ if ($7 > 0) print}')

echo "delay message cnt:$delayCnt, delay time=$delaySec s"

metric=CM-MQ-Message-Accumulate
if [[ $delaySec > $timeThreshold ]] || [[ $delayCnt > $cntThreshold ]];then
  echo "Alert !!!!"
  tags="DataType=alarmData,threshold=${timeThreshold}s-$cntThreshold"
  curl -X POST -H "Content-Type:application/json; charset=utf-8" -d \
    "[{\"Metric\":\"$metric\", \
       \"Tags\":\"$tags\", \
       \"Value\":{\"time\":$delaySec,\"cnt\":$delayCnt}, \
       \"Timestamp\":$now, \
       \"Endpoint\":\"$consumerGroup\", \
       \"CounterType\":\"GAUGE\", \
       \"Step\":60}]" http://127.0.0.1:2121/v1/push
fi