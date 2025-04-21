#!/bin/sh -e
echo "Build project"
make clean
make build
make clean
echo "OK"
