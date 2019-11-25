#!/bin/bash

repeat() {
    for i in $(seq 1 $1)
    do
        echo -n "."
    done
}

MAXNAMELEN=0
for filename in tests/*.488; do
    length=${#filename}
    if [ $MAXNAMELEN \< $length ]
    then
        MAXNAMELEN=$length
    fi
done
MAXNAMELEN=$((MAXNAMELEN + 10))

echo -e "===== Tests =====\n"
for filename in tests/*.488; do
    msg="Test \"$filename\""
    echo -n -e $msg
    length=${#msg}
    repeat $((MAXNAMELEN - length))

    RESULT=$(echo 7 12 | java -jar dist/compiler488.jar $filename 2> .tmp)
    DEBUG=$(cat .tmp)
    startmsp=$(echo "$DEBUG" | sed -e "1,1d" | head -n 1 | cut -d' ' -f9)
    endmsp=$(echo "$DEBUG" | sed -e "1,5d" | head -n 1 | cut -d' ' -f8)
    execResult=$(echo "$RESULT" | sed -e "1,2d" | head -c -18)
    
    if [ "$startmsp" = "$endmsp" ] && [ "<$execResult>" = $'<End of Compilation\nBegin Execution>' ]
    then
        echo "PASSED"
    else
        echo "FAILED"
    fi
done
rm .tmp