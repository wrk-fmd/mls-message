#!/bin/bash

( socat -d -d pty,raw,echo=0,link=/dev/ttyUSB60 pty,raw,echo=0,link=/dev/ttyIN60 ) &

sleep 3
chmod 666 /dev/ttyUSB60

function sendcall() {
  printf "\x02T1%02d\x03" $((($3 * 3 + 1) % 100))
  printf "\x02%s%s%s%02d\x03" "$1" "$2" "$2" $((($3 * 3 + 2) % 100))
  printf "\x02T0%02d\x03" $((($3 * 3 + 3) % 100))
}

seq=0
for i in $(seq -w 9999999); do
  sendcall "I1" "$i" $seq > /dev/ttyIN60
  sleep 0.05
  ((seq+=1))
done
