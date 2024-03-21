#!/bin/bash

cd ../physician_component

mvn package

java -jar ../physician_component/target/physician_component-0.0.1-SNAPSHOT.jar
