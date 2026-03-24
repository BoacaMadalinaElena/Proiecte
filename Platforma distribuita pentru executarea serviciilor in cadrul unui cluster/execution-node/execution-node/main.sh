#!/bin/bash

rm *.txt

OUTPUT_FILE="output_file.txt"

PROCESS_NAME="java"
PID=91540
PID2=91540

if ! ps -p $PID > /dev/null 2>&1; then
    echo "Procesul cu PID $PID nu a fost gasit."
    exit 1
fi



monitor_process() {
    while ps -p $PID > /dev/null 2>&1; do
        CPU=$(ps -p $PID -o %cpu --no-headers)
        MEM_KB=$(ps -p $PID -o rss --no-headers)
        MEM_MB=$(echo "scale=2; $MEM_KB/1024" | bc)

        echo "$CPU,$MEM_MB" >> $OUTPUT_FILE

        sleep 5
    done

    echo "Procesul cu PID $PID s-a terminat." >> $OUTPUT_FILE
}

monitor_process
