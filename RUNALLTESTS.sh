#!/bin/bash
echo -e "===== Passing cases =====\n"
for filename in tests/pass/*.488; do
    echo -n -e "Test \""$filename"\"..... "
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
    echo -n -e "Test \""$filename"\"..... "
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