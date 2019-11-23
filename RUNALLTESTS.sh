#!/bin/bash
echo -e "===== Tests =====\n"
for filename in tests/*.488; do
    echo -e "----- Case \"" $filename "\" -----\n"
    RESULT=$(echo 7 12 | java -jar dist/compiler488.jar $filename 2> .tmp)
    DEBUG=$(cat .tmp)
    startmsp=$(echo "$DEBUG" | sed -e "1,1d" | head -n 1 | cut -d' ' -f9)
    endmsp=$(echo "$DEBUG" | sed -e "1,5d" | head -n 1 | cut -d' ' -f8)
    if [ "$startmsp" = "$endmsp" ]
    then
        echo ""
    else
    fi
    echo "StartMSP: " $startmsp
    echo "EndMSP: " $endmsp
    echo "$RESULT"
done
rm .tmp