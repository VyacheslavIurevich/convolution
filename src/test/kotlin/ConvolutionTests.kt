import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class ConvolutionTests {
    private val solver = ConvolutionSolver()
    private val handler = BMPHandler()
    private val inputFiles: Array<out File?>? = File("src/test/resources/in/pics").listFiles()

    private fun filterTest(filterName: String) {
        if (inputFiles != null) {
            for (implementation in ConvolutionImplementation.entries) {
                for (file in inputFiles) {
                    val fileName = file?.getName()?.dropLast(4)
                    val input = handler.readBMP("src/test/resources/in/pics/$fileName.bmp")
                    val filteringInfo = solver.availableFilters[filterName]
                    if (filteringInfo != null) {
                        val output = solver.convolve(input, filteringInfo, implementation)
                        val reference = handler.readBMP("src/test/resources/ref_out/$fileName/$filterName.bmp")
                        checkBMPEquality(reference, output)
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Convolution with ID filter doesn't change image")
    fun checkIDFilter() {
        filterTest("id")
    }

    @Test
    @DisplayName("Blur 3x3 filter")
    fun checkBlur3x3Filter() {
        filterTest("blur-3x3")
    }

    @Test
    @DisplayName("Blur 5x5 filter")
    fun checkBlur5x5Filter() {
        filterTest("blur-5x5")
    }

    @Test
    @DisplayName("Gaussian Blur 3x3 filter")
    fun checkGaussianBlur3x3Filter() {
        filterTest("gaussian-blur-3x3")
    }

    @Test
    @DisplayName("Gaussian Blur 5x5 filter")
    fun checkGaussianBlur5x5Filter() {
        filterTest("gaussian-blur-5x5")
    }

    @Test
    @DisplayName("Motion Blur filter")
    fun checkMotionBlurFilter() {
        filterTest("motion-blur")
    }

    @Test
    @DisplayName("Horizontal edges finding filter")
    fun findHorizontalEdgesFilter() {
        filterTest("find-horizontal-edges")
    }

    @Test
    @DisplayName("Vertical edges finding filter")
    fun findVerticalEdgesFilter() {
        filterTest("find-vertical-edges")
    }

    @Test
    @DisplayName("Inclined edges finding filter")
    fun findInclinedEdgesFilter() {
        filterTest("find-inclined-edges")
    }

    @Test
    @DisplayName("All edges finding filter")
    fun findAllEdgesFilter() {
        filterTest("find-all-edges")
    }

    @Test
    @DisplayName("First Sharpen filter")
    fun firstSharpenFilter() {
        filterTest("first-sharpen")
    }

    @Test
    @DisplayName("Second Sharpen filter")
    fun secondSharpenFilter() {
        filterTest("second-sharpen")
    }

    @Test
    @DisplayName("Third Sharpen filter")
    fun thirdSharpenFilter() {
        filterTest("third-sharpen")
    }

    @Test
    @DisplayName("Emboss 3x3 filter")
    fun emboss3x3Filter() {
        filterTest("emboss-3x3")
    }

    @Test
    @DisplayName("Emboss 5x5 filter")
    fun emboss5x5Filter() {
        filterTest("emboss-5x5")
    }

    @Test
    @DisplayName("Mean filter")
    fun meanFilter() {
        filterTest("mean")
    }
}
