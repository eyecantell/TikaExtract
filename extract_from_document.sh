#!/bin/bash
mvn exec:java -Dexec.mainClass="TikaExtract" -Dexec.args="$1"