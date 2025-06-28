# convolution
![CI](https://github.com/VyacheslavIurevich/convolution/actions/workflows/ci.yml/badge.svg)

Bitmap image convolution C implementation.
 ## Table of contents
* [Functionality](#functionality)
* [File structure](#file-structure)
* [How to use](#how-to-use)
* [Author](#author)
* [Project status](#status)
* [License](#license)
## Functionality <a name="functionality"></a>
Functionality description --- TODO
## File structure <a name="file-structure"></a>
```
├──scripts
│   ├──ci
│   │  ├── build.sh // checks the project build
│   │  ├── check-formatting.sh // checks .c and .h files formatting
│   │  ├── check-leaks.sh // checks for memory leaks and race conditions
│   │  ├── checkmake.sh // analyzes makefile
│   │  ├── lint.sh // lints .c and .h files
│   │  ├── shellcheck.sh // analyzes .sh files
│   │  ├── tests.sh // runs tests and evaluates coverage
│   ├── format.sh // formats all .c and .h files according to .clang-format config file
```
src and tests description --- TODO
## How to use <a name="how-to-use"></a>
See [DOCS.md](./DOCS.md)
## Author <a name="author"></a>
* Vyacheslav Kochergin. [GitHub](https://github.com/VyacheslavIurevich), [Telegram](https://t.me/se4life).
## Project status <a name="status"></a>
Sequential and parallel implementation (tasks 1, 2) --- in development. 
## License <a name="license"></a>
See [LICENSE](./LICENSE)
