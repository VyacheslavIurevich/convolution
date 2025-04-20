#!/bin/sh -e
echo "Run tests"
make test
make clean
echo "OK"
