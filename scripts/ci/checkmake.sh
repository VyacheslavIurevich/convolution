#!/bin/sh -e
echo "Check makefile"
go run github.com/mrtazz/checkmake/cmd/checkmake@latest Makefile 2>/dev/null
echo "OK"
