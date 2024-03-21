#!/bin/bash

cd ../appointment-component/

mvn package

java -jar ../appointment-component/target/appointment-component-0.0.1-SNAPSHOT.jar
