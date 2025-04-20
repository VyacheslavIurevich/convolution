#!/bin/sh -e
echo "Build project"
make build
make clean
echo "OK"
