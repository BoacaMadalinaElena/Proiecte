#!/bin/bash

cd ../idm_component_client

mvn package

java -jar ../idm_component_client/target/idm_component-0.0.1-SNAPSHOT.jar
