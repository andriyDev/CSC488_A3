#!/bin/bash

repeat() {
    for i in $(seq 1 $1)
    do
        echo -n "."
    done
}

MAXNAMELEN=0
for filename in tests/pass/*.488 tests/fail/*.488; do
    length=${#filename}
    if [ $MAXNAMELEN \< $length ]
    then
        MAXNAMELEN=$length
    fi
done
MAXNAMELEN=$((MAXNAMELEN + 10))

echo -e "===== Passing cases =====\n"
for filename in tests/pass/*.488; do
    msg="Test \"$filename\""
    echo -n -e $msg
    length=${#msg}
    repeat $((MAXNAMELEN - length))
    java -jar dist/compiler488.jar -X "$filename" 2> ".tmp" > /dev/null
    ERROR=$(<.tmp)
    if [ -z "$ERROR" ]
    then
        echo -n -e "PASSED\n"
    else
        echo -n -e "FAILED\n"
        echo $ERROR
    fi
done
echo -e "\n===== Failing cases =====\n"
for filename in tests/fail/*.488; do
    msg="Test \"$filename\""
    echo -n -e $msg
    length=${#msg}
    repeat $((MAXNAMELEN - length))
    java -jar dist/compiler488.jar -X "$filename" 2> ".tmp" > /dev/null
    ERROR=$(<.tmp)
    if [ -n "$ERROR" ]
    then
        echo -n -e "PASSED\n"
    else
        echo -n -e "FAILED\n"
    fi
done
rm -rf ".tmp"