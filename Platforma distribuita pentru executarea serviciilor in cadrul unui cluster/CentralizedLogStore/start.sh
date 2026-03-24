#!/bin/bash

clear

mvn package

java  -jar ./target/CentralizedLogStore-1.0-SNAPSHOT.jar
