import java.io.File
import org.example.BMPHandler
import org.example.ConvolutionSolver
import org.example.FilteringInfo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import org.bytedeco.opencv.opencv_core.Mat

class ConvolutionTests {
    private val solver = ConvolutionSolver()
    private val handler = BMPHandler()
    private val inputFiles: Array<out File?>? = File("src/test/resources/in").listFiles()
    private val filters = mapOf(
        "id" to FilteringInfo(
            arrayOf(
                doubleArrayOf(0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 1.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0)
            ), 1.0, 0.0
        ),
        "blur-3x3" to FilteringInfo(
            arrayOf(
                doubleArrayOf(0.0, 0.2, 0.0),
                doubleArrayOf(0.2, 0.2, 0.2),
                doubleArrayOf(0.0, 0.2, 0.0)
            ), 1.0, 0.0
        ),
        "blur-5x5" to FilteringInfo(
            arrayOf(
                doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0),
                doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0),
                doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0),
                doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0),
                doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0)
            ), 1.0 / 13.0, 0.0
        ),
        "gaussian-blur-3x3" to FilteringInfo(
            arrayOf(
                doubleArrayOf(1.0, 2.0, 1.0),
                doubleArrayOf(2.0, 4.0, 2.0),
                doubleArrayOf(1.0, 2.0, 1.0)
            ), 1.0 / 16.0, 0.0
        ),
        "gaussian-blur-5x5" to FilteringInfo(
            arrayOf(
                doubleArrayOf(1.0, 4.0, 6.0, 4.0, 1.0),
                doubleArrayOf(4.0, 16.0, 24.0, 16.0, 4.0),
                doubleArrayOf(6.0, 24.0, 36.0, 24.0, 6.0),
                doubleArrayOf(4.0, 16.0, 24.0, 16.0, 4.0),
                doubleArrayOf(1.0, 4.0, 6.0, 4.0, 1.0)
            ), 1.0 / 256.0, 0.0
        ),
        "motion-blur" to FilteringInfo(
            arrayOf(
                doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
            ), 1.0 / 9.0, 0.0
        ),
        "find-horizontal-edges" to FilteringInfo(
            arrayOf(
                doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 2.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0)
            ), 1.0, 0.0
        ),
        "find-vertical-edges" to FilteringInfo(
            arrayOf(
                doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 4.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0)
            ), 1.0, 0.0
        ),
        "find-inclined-edges" to FilteringInfo(
            arrayOf(
                doubleArrayOf(-1.0, 0.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, -2.0, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 6.0, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, -2.0, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, -1.0)
            ), 1.0, 0.0
        ),
        "find-all-edges" to FilteringInfo(
            arrayOf(
                doubleArrayOf(-1.0, -1.0, -1.0),
                doubleArrayOf(-1.0, 8.0, -1.0),
                doubleArrayOf(-1.0, -1.0, -1.0)
            ), 1.0, 0.0
        ),
        "first-sharpen" to FilteringInfo(
            arrayOf(
                doubleArrayOf(-1.0, -1.0, -1.0),
                doubleArrayOf(-1.0, 9.0, -1.0),
                doubleArrayOf(-1.0, -1.0, -1.0)
            ), 1.0, 0.0
        ),
        "second-sharpen" to FilteringInfo(
            arrayOf(
                doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0),
                doubleArrayOf(-1.0, 2.0, 2.0, 2.0, -1.0),
                doubleArrayOf(-1.0, 2.0, 8.0, 2.0, -1.0),
                doubleArrayOf(-1.0, 2.0, 2.0, 2.0, -1.0),
                doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0)
            ), 1.0 / 8.0, 0.0
        ),
        "third-sharpen" to FilteringInfo(
            arrayOf(
                doubleArrayOf(1.0, 1.0, 1.0),
                doubleArrayOf(1.0, -7.0, 1.0),
                doubleArrayOf(1.0, 1.0, 1.0)
            ), 1.0, 0.0
        ),
        "emboss-3x3" to FilteringInfo(
            arrayOf(
                doubleArrayOf(-1.0, -1.0, 0.0),
                doubleArrayOf(-1.0, 0.0, 1.0),
                doubleArrayOf(0.0, 1.0, 1.0)
            ), 1.0, 128.0
        ),
        "emboss-5x5" to FilteringInfo(
            arrayOf(
                doubleArrayOf(-1.0, -1.0, -1.0, -1.0, 0.0),
                doubleArrayOf(-1.0, -1.0, -1.0, 0.0, 1.0),
                doubleArrayOf(-1.0, -1.0, 0.0, 1.0, 1.0),
                doubleArrayOf(-1.0, 0.0, 1.0, 1.0, 1.0),
                doubleArrayOf(0.0, 1.0, 1.0, 1.0, 1.0)
            ), 1.0, 128.0
        ),
        "mean" to FilteringInfo(
            arrayOf(
                doubleArrayOf(1.0, 1.0, 1.0),
                doubleArrayOf(1.0, 1.0, 1.0),
                doubleArrayOf(1.0, 1.0, 1.0)
            ), 1.0 / 9.0, 0.0
        ),
    )

    private fun checkBMPEquality(bmp1: Mat, bmp2: Mat) {
        val w = bmp1.cols()
        val h = bmp1.rows()
        assertEquals(w, bmp2.cols())
        assertEquals(h, bmp2.rows())
        for (x in 0 until w) {
            for (y in 0 until h) {
                for (idx in 0..2) {
                    val firstValue = bmp1.ptr(y, x).get(idx.toLong())
                    val secondValue = bmp2.ptr(y, x).get(idx.toLong())
                    assertEquals(firstValue, secondValue)
                }
            }
        }
    }

    private fun filterTest(filterName: String) {
        if (inputFiles != null) {
            for (file in inputFiles) {
                val fileName = file?.getName()?.dropLast(4)
                val input = handler.readBMP("src/test/resources/in/$fileName.bmp")
                val filteringInfo = filters[filterName]
                if (filteringInfo != null) {
                    val output = solver.convolve(input, filteringInfo)
                    val reference = handler.readBMP("src/test/resources/ref_out/$fileName/$filterName.bmp")
                    checkBMPEquality(reference, output)
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
