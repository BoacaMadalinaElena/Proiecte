#!/bin/bash

OUTPUT_FILE="output_file.txt"
OUTPUT_FILE2="output_file2.txt"
OUTPUT_FILE_DOCKER="output_fileDocker.txt"

PROCESS_NAME="java"
PID=10334
PID2=10898

if ! ps -p $PID > /dev/null 2>&1; then
    echo "Procesul cu PID $PID nu a fost gasit."
    exit 1
fi

if ! ps -p $PID2 > /dev/null 2>&1; then
    echo "Procesul cu PID $PID2 nu a fost gasit."
    exit 1
fi

get_docker_stats() {
    docker stats --no-stream --format "table {{.Container}}\t{{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}\t{{.PIDs}}" >> $OUTPUT_FILE_DOCKER
}


monitor_process() {
    while ps -p $PID > /dev/null 2>&1; do
        CPU=$(ps -p $PID -o %cpu --no-headers)
        MEM_KB=$(ps -p $PID -o rss --no-headers)
        MEM_MB=$(echo "scale=2; $MEM_KB/1024" | bc)

        echo "$CPU,$MEM_MB" >> $OUTPUT_FILE

        CPU=$(ps -p $PID2 -o %cpu --no-headers)
        MEM_KB=$(ps -p $PID2 -o rss --no-headers)
        MEM_MB=$(echo "scale=2; $MEM_KB/1024" | bc)

        echo "$CPU,$MEM_MB" >> $OUTPUT_FILE2

        echo "---------------------------------------" >> $OUTPUT_FILE_DOCKER
        echo "Timestamp: $(date)" >> $OUTPUT_FILE_DOCKER
        get_docker_stats


        sleep 5
    done

    echo "Procesul cu PID $PID s-a terminat." >> $OUTPUT_FILE
}

monitor_process
