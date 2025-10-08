import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.IllegalArgumentException

class ImageTaskTests {
    private val id =
        FilteringInfo(
            arrayOf(
                doubleArrayOf(0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 1.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0),
            ),
            1.0,
            0.0,
        )

    @Test
    @DisplayName("Non-BMP input file ImageTask creation fails with exception")
    fun checkNonBMPInputImageTask() {
        assertThrows<IllegalArgumentException> {
            ImageTask(
                "src/test/resources/in/pics",
                "src/test/resources/output.bmp",
                id,
                ConvolutionImplementation.SEQUENTIAL,
            )
        }
    }

    @Test
    @DisplayName("Non-BMP output file ImageTask creation fails with exception")
    fun checkNonBMPOutputImageTask() {
        assertThrows<IllegalArgumentException> {
            ImageTask(
                "src/test/resources/in/pics/example.bmp",
                "src/test/resources",
                id,
                ConvolutionImplementation.SEQUENTIAL,
            )
        }
    }

    @Test
    @DisplayName("Non-existent input file ImageTask creation fails with exception")
    fun checkNonExistentInputImageTask() {
        assertThrows<IllegalArgumentException> {
            ImageTask(
                "src/test/resources/in/pics/non-existent-file.bmp",
                "src/test/resources/output.bmp",
                id,
                ConvolutionImplementation.SEQUENTIAL,
            )
        }
    }
}
