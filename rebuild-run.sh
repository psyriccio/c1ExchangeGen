#!/bin/bash
gradle clean
gradle shadowJar
reset
java -jar ./build/libs/c1ExchangeGen-latest-SNAPSHOT-all.jar gui ./alucom.xml ./resurs.xml ./map.xml
