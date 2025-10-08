import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.test.assertNotNull

class BMPHandlerTests {
    val handler = BMPHandler()

    @Test
    @DisplayName("Valid BMP open is not null")
    fun checkValidBMPRead() {
        assertNotNull(handler.readBMP("src/test/resources/in/pics/example.bmp"))
    }

    @Test
    @DisplayName("Non-BMP file read fails with exception")
    fun checkNonBMPRead() {
        assertThrows<IllegalArgumentException> {
            handler.readBMP("src/test/resources/in/pics")
        }
    }

    @Test
    @DisplayName("Non-existent BMP file read fails with exception")
    fun checkNonExistentRead() {
        assertThrows<IllegalArgumentException> {
            handler.readBMP("src/test/resources/in/pics/non-existent-file.bmp")
        }
    }

    @Test
    @DisplayName("Write BMP to non-BMP file fails with exception")
    fun checkNonBMPWrite() {
        val image = handler.readBMP("src/test/resources/in/pics/example.bmp")
        assertThrows<IllegalArgumentException> {
            handler.writeBMP("src/test/resources/output.jpg", image)
        }
    }

    @Test
    @DisplayName("Write BMP to non-existent folder fails with exception")
    fun checkNonExistentFolderWrite() {
        val image = handler.readBMP("src/test/resources/in/pics/example.bmp")
        assertThrows<IllegalArgumentException> {
            handler.writeBMP("src/test/resources/non-existent-folder/output.bmp", image)
        }
    }

    @Test
    @DisplayName("BMP read & write composition works correctly")
    fun checkBMPReadWriteComposition() {
        val image = handler.readBMP("src/test/resources/in/pics/example.bmp")
        handler.writeBMP("src/test/resources/example_output.bmp", image)
        val image2 = handler.readBMP("src/test/resources/example_output.bmp")
        checkBMPEquality(image, image2)
        val image2Path = Path("src/test/resources/example_output.bmp")
        image2Path.deleteIfExists()
    }
}
