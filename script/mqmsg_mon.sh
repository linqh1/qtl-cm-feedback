#!/bin/bash
cd /home/rocketmq/rocketmq-all-4.6.0-bin-release/bin/

consumerGroup=$1
timeThreshold=$2
cntThreshold=$3
echo "######## Message Delay Monitor ($consumerGroup) ########"
echo "######## Alert Threshold: DelayTime>=$timeThreshold Or DelayCnt>=$cntThreshold ########"

mqoutput=$(./mqadmin consumerProgress -n 127.0.0.1:9876 -g "$consumerGroup" 2>&1)
echo "$mqoutput"

delayCnt=$(echo "$mqoutput" | grep "Diff Total" | tail -n 1 | sed 's/Diff Total: //g')
delaySec=0

progressDetail=$(echo "$mqoutput" | sed -n '/#Topic/,/^$/p' | tail -n +2)
now=$(date +%s)
function alert()
{
  echo "Alert! $1"
  alertTags="DataType=alarmData,threshold=${timeThreshold}s-$cntThreshold,Param=$1"
  #echo "$alertTags, $delaySec, $delayCnt, $now, $consumerGroup"
  alertCnt=$delayCnt
  if [ "$alertCnt" =  "" ];then
    alertCnt=0
  fi
  curl -X POST -H "Content-Type:application/json; charset=utf-8" -d \
      "[{\"Metric\":\"CM-MQ-Message-Accumulate\", \
         \"Tags\":\"$alertTags\", \
         \"Value\":{\"time\":$delaySec,\"cnt\":$alertCnt}, \
         \"Timestamp\":$now, \
         \"Endpoint\":\"$consumerGroup\", \
         \"CounterType\":\"GAUGE\", \
         \"Step\":60}]" http://127.0.0.1:2121/v1/push
}

if [ "$progressDetail" = "" ];then
  echo "$(date) $mqoutput" >> ~/mqmsg_mon.log
  alert "AbnormalOutput"
  exit 1
fi

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
  else
    alert "BrokerDelay"
  fi
done< <(echo "$progressDetail" | awk '{ if ($7 > 0) print}')

echo "delay message cnt:$delayCnt, delay time=$delaySec s"

if [ $delaySec -ge $timeThreshold ] || [ $delayCnt -ge $cntThreshold ];then
  alert "MessageDelay"
fi