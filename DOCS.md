# convolution docs
## Build dependencies
* clang 20.1.2
* make 4.4.1
## Tests dependencies
* cmocka 1.1.7
## CI dependencies
* llvm 20.1.2 (exactly llvm-profdata, llvm-cov) --- for coverage
* valgrind 3.24.0 --- for memory leaks and race conditions detection
* bear 3.1.5 and clang-tidy (llvm 20.1.2) --- for .c and .h files linting
* clang-format 20.1.2 --- for .c and .h files formatting
* shellcheck 0.10.0 --- for .sh files analysis
* go 1.24 --- for checkmake (makefile analysis)
Setup and usage --- TODO
