#!/bin/bash

cd ../idm_component

mvn package

java -jar ../idm_component/target/idm_component-0.0.1-SNAPSHOT.jar
