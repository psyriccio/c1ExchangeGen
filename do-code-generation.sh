#!/bin/bash
xjc -d ./src/ -p c1exchangegen.generated -xmlschema -nv -verbose ./src/c1exchangegen/xsd/ExchangeMapping.xsd
