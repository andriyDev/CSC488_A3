#!/bin/bash
echo -e "===== Tests =====\n"
for filename in tests/*.488; do
    echo -e "----- Case \"" $filename "\" -----\n"
    java -jar dist/compiler488.jar $filename
done