#include <setjmp.h>
#include <stdarg.h>
#include <stddef.h>
#include <cmocka.h>
#include "../src/lib.h"

static void test_function_a_returns_5(void **state) {
  (void)state;
  assert_int_equal(a(), 5);
}

int main(void) {
  const struct CMUnitTest tests[] = {
      cmocka_unit_test(test_function_a_returns_5),
  };
  return cmocka_run_group_tests(tests, NULL, NULL);
}
