#!/bin/bash
gradle clean
gradle shadowJar
reset
java -jar ./build/libs/c1ExchangeGen-latest-SNAPSHOT-all.jar map ./alucom.xml ./resurs.xml ./map.xml
