.POSIX:

SHELL = /bin/sh

CC = clang
CFLAGS = -Wall -Wextra -Werror -pedantic -O2
# CMocka compiler flags
CMOCKA_CFLAGS = $(shell pkg-config --cflags cmocka 2>/dev/null || echo "-I/usr/include")
# CMocka linker flags
CMOCKA_LIBS = $(shell pkg-config --libs cmocka 2>/dev/null || echo "-L/usr/lib -lcmocka")

SRC_DIR = src
TESTS_DIR = tests
BIN_DIR = bin
OBJ_DIR = obj
COV_DIR = coverage

TARGET = $(BIN_DIR)/main
TEST_TARGET = $(BIN_DIR)/tests_runner

.PHONY: all build build_coverage build_tests test clean

all: build test

build: $(TARGET)

build_coverage: CFLAGS += -fprofile-instr-generate -fcoverage-mapping
build_coverage: $(TARGET)

build_tests: CFLAGS += -fprofile-instr-generate -fcoverage-mapping
build_tests: $(TEST_TARGET)

test: $(TEST_TARGET)
	./$(TEST_TARGET)

$(BIN_DIR) $(OBJ_DIR) $(COV_DIR):
	@mkdir -p $@

$(TARGET): $(SRC_DIR)/main.c | $(BIN_DIR)
	@$(CC) $(CFLAGS) $< -o $@

$(OBJ_DIR)/lib.o: $(SRC_DIR)/lib.c | $(OBJ_DIR)
	@$(CC) $(CFLAGS) $(CMOCKA_CFLAGS) -c $< -o $@

$(TEST_TARGET): $(TESTS_DIR)/test_lib.c $(OBJ_DIR)/lib.o | $(BIN_DIR)
	@$(CC) $(CFLAGS) $(CMOCKA_CFLAGS) -o $@ $^ $(CMOCKA_LIBS)

clean:
	-@rm -rf $(BIN_DIR) $(OBJ_DIR) $(COV_DIR)
