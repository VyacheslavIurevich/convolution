.POSIX:

SHELL = /bin/sh

CC = gcc
CFLAGS = -Wall -Wextra -pedantic -O2
# get CMocka compiler flags
CMOCKA_CFLAGS = $(shell pkg-config --cflags cmocka 2>/dev/null || echo "-I/usr/include")
# get CMocka linker flags
CMOCKA_LIBS = $(shell pkg-config --libs cmocka 2>/dev/null || echo "-L/usr/lib -lcmocka")

SRC_DIR = src
TESTS_DIR = tests
BIN_DIR = bin
OBJ_DIR = obj

TARGET = $(BIN_DIR)/main
TEST_TARGET = $(BIN_DIR)/test_runner

all: build test

build: $(TARGET)

build_test: $(TEST_TARGET)

test: $(TEST_TARGET)
	@./$(TEST_TARGET)

$(BIN_DIR) $(OBJ_DIR):
	@mkdir -p $@

$(TARGET): $(SRC_DIR)/main.c | $(BIN_DIR)
	@$(CC) $(CFLAGS) $< -o $@

# create object file for tests
$(OBJ_DIR)/lib.o: $(SRC_DIR)/lib.c | $(OBJ_DIR)
	@$(CC) $(CFLAGS) $(CMOCKA_CFLAGS) -c $< -o $@

# link tests binary
$(TEST_TARGET): $(TESTS_DIR)/test_lib.c $(OBJ_DIR)/lib.o | $(BIN_DIR)
	@$(CC) $(CFLAGS) $(CMOCKA_CFLAGS) -o $@ $^ $(CMOCKA_LIBS)

clean:
	@rm -rf $(BIN_DIR) $(OBJ_DIR)

.PHONY: all build build_test test clean
