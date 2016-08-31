#!/bin/bash
gradle clean
gradle shadowJar
reset
java -XX:+UseG1GC -jar ./build/libs/c1ExchangeGen-latest-SNAPSHOT-all.jar gui ./alucom.xml ./buh.xml ./map.xml
