#!/usr/bin/env bash

OUTPUT=./Tests/outputs
TMP=./Tests/tmp

if [ -d ${OUTPUT} ]; then
rm -rf ${OUTPUT:?}/*/
fi

if [ -d ${TMP} ]; then
rm -rf ${TMP:?}/*/
fi