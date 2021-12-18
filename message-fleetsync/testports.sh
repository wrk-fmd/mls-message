#!/bin/bash

( socat -d -d pty,raw,echo=0,link=/dev/ttyUSB50 pty,raw,echo=0,link=/dev/ttyIN50 ) &
( socat -d -d pty,raw,echo=0,link=/dev/ttyUSB51 pty,raw,echo=0,link=/dev/ttyIN51 ) &

sleep 3
chmod 666 /dev/ttyUSB50 /dev/ttyUSB51

function sendcall() {
  printf "\x02T1%02d\x03" $((($3 * 3 + 1) % 100))
  printf "\x02%s%s%s%02d\x03" "$1" "$2" "$2" $((($3 * 3 + 2) % 100))
  printf "\x02T0%02d\x03" $((($3 * 3 + 3) % 100))
}

seq=0
while true ; do
  if [ $RANDOM -le 3000 ]; then
    sendcall "E" "3456789" $seq > /dev/ttyIN50
  else
    sendcall "I1" "3456789" $seq > /dev/ttyIN50
  fi

  sleep $((10+RANDOM/6000))

  if [ $RANDOM -le 3000 ]; then
    sendcall "E" "1234567" $seq > /dev/ttyIN51
  else
    sendcall "I1" "1234567" $seq > /dev/ttyIN51
  fi

  sleep $((30+RANDOM/3000))

  ((seq+=1))
done;
