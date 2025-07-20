import org.example.Example
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ExampleTests {
    private val a = Example()

    @Test
    @DisplayName("example test")
    fun exampleTest() {
        assertEquals(5, a.main())
    }
}
