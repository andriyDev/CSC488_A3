#!/bin/bash

repeat() {
    for i in $(seq 1 $1)
    do
        echo -n "."
    done
}

MAXNAMELEN=0
for filename in tests/passing/*.488 tests/failing/*.488; do
    length=${#filename}
    if [ $MAXNAMELEN \< $length ]
    then
        MAXNAMELEN=$length
    fi
done
MAXNAMELEN=$((MAXNAMELEN + 10))

PASSCOUNT=0
PASSTESTS=0
echo -e "===== Passing cases =====\n"
for filename in tests/passing/*.488; do
    ((PASSTESTS ++))

    msg="Test \"$filename\""
    echo -n -e $msg
    length=${#msg}
    repeat $((MAXNAMELEN - length))
    java -jar dist/compiler488.jar -X "$filename" 2> ".tmp" > /dev/null
    ERROR=$(<.tmp)
    if [ -z "$ERROR" ]
    then
        echo -n -e "PASSED\n"
        ((PASSCOUNT ++))
    else
        echo -n -e "FAILED\n"
        echo $ERROR
    fi
done

FAILCOUNT=0
FAILTESTS=0
echo -e "\n===== Failing cases =====\n"
for filename in tests/failing/*.488; do
    ((FAILTESTS ++))
    msg="Test \"$filename\""
    echo -n -e $msg
    length=${#msg}
    repeat $((MAXNAMELEN - length))
    java -jar dist/compiler488.jar -X "$filename" 2> ".tmp" > /dev/null
    ERROR=$(<.tmp)
    if [ -n "$ERROR" ]
    then
        echo -n -e "PASSED\n"
        echo "$ERROR"
        ((FAILCOUNT ++))
    else
        echo -n -e "FAILED\n"
    fi
done

echo -e "\n\n=======Passing cases: $PASSCOUNT / $PASSTESTS =========\n"
echo -e "=======Failing cases: $FAILCOUNT / $FAILTESTS =========\n"

rm -rf ".tmp"