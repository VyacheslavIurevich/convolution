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

    private fun filterTest(filteringInfo: FilteringInfo, refName: String) {
        if (inputFiles != null) {
            for (file in inputFiles) {
                val fileName = file?.getName()?.dropLast(4)
                val input = handler.readBMP("src/test/resources/in/$fileName.bmp")
                val output = solver.convolve(input, filteringInfo)
                println("src/test/resources/ref_out/$fileName/$refName.bmp")
                val reference = handler.readBMP("src/test/resources/ref_out/$fileName/$refName.bmp")
                checkBMPEquality(reference, output)
            }
        }

    }

    private fun help(filteringInfo: FilteringInfo, name: String, picname: String) {
        val input = handler.readBMP("src/test/resources/in/$picname.bmp")
        val output = solver.convolve(input, filteringInfo)
        handler.writeBMP("src/test/resources/ref_out/$picname/$name.bmp", output)
    }

    @Test
    @DisplayName("Convolution with ID filter doesn't change image")
    fun checkIDFilter() {
        val idFilter = arrayOf(
            doubleArrayOf(0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0)
        )
        val idFilteringInfo = FilteringInfo(idFilter, 1.0, 0.0)
        filterTest(idFilteringInfo, "id")
    }

    @Test
    @DisplayName("Blur 3x3 filter")
    fun checkBlur3x3Filter() {
        val blur3x3Filter = arrayOf(
            doubleArrayOf(0.0, 0.2, 0.0),
            doubleArrayOf(0.2, 0.2, 0.2),
            doubleArrayOf(0.0, 0.2, 0.0)
        )
        val blur3x3FilteringInfo = FilteringInfo(blur3x3Filter, 1.0, 0.0)
        filterTest(blur3x3FilteringInfo, "blur-3x3")
    }

    @Test
    @DisplayName("Blur 5x5 filter")
    fun checkBlur5x5Filter() {
        val blur5x5Filter = arrayOf(
            doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0),
            doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0),
            doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0),
        )
        val blur5x5FilteringInfo = FilteringInfo(blur5x5Filter, 1.0 / 13.0, 0.0)
        filterTest(blur5x5FilteringInfo, "blur-5x5")
    }

    @Test
    @DisplayName("Gaussian Blur 3x3 filter")
    fun checkGaussianBlur3x3Filter() {
        val gaussianBlur3x3Filter = arrayOf(
            doubleArrayOf(1.0, 2.0, 1.0),
            doubleArrayOf(2.0, 4.0, 2.0),
            doubleArrayOf(1.0, 2.0, 1.0)
        )
        val gaussianBlur3x3FilteringInfo = FilteringInfo(gaussianBlur3x3Filter, 1.0 / 16.0, 0.0)
        filterTest(gaussianBlur3x3FilteringInfo, "gaussian-blur-3x3")
    }

    @Test
    @DisplayName("Gaussian Blur 5x5 filter")
    fun checkGaussianBlur5x5Filter() {
        val gaussianBlur5x5Filter = arrayOf(
            doubleArrayOf(1.0, 4.0, 6.0, 4.0, 1.0),
            doubleArrayOf(4.0, 16.0, 24.0, 16.0, 4.0),
            doubleArrayOf(6.0, 24.0, 36.0, 24.0, 6.0),
            doubleArrayOf(4.0, 16.0, 24.0, 16.0, 4.0),
            doubleArrayOf(1.0, 4.0, 6.0, 4.0, 1.0),
        )
        val gaussianBlur5x5FilteringInfo = FilteringInfo(gaussianBlur5x5Filter, 1.0 / 256.0, 0.0)
        filterTest(gaussianBlur5x5FilteringInfo, "gaussian-blur-5x5")
    }

    @Test
    @DisplayName("Motion Blur filter")
    fun checkMotionBlurFilter() {
        val motionBlurFilter = arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        )
        val motionBlurFilteringInfo = FilteringInfo(motionBlurFilter, 1.0 / 9.0, 0.0)
        filterTest(motionBlurFilteringInfo, "motion-blur")
    }

    @Test
    @DisplayName("Horizontal edges finding filter")
    fun findHorizontalEdgesFilter() {
        val findHorizontalEdgesFilter = arrayOf(
            doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 2.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0),
        )
        val findHorizontalEdgesFilteringInfo = FilteringInfo(findHorizontalEdgesFilter, 1.0, 0.0)
        filterTest(findHorizontalEdgesFilteringInfo, "find-horizontal-edges")
    }

    @Test
    @DisplayName("Vertical edges finding filter")
    fun findVerticalEdgesFilter() {
        val findVerticalEdgesFilter = arrayOf(
            doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 4.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
        )
        val findVerticalEdgesFilteringInfo = FilteringInfo(findVerticalEdgesFilter, 1.0, 0.0)
        filterTest(findVerticalEdgesFilteringInfo, "find-vertical-edges")
    }

    @Test
    @DisplayName("Inclined edges finding filter")
    fun findInclinedEdgesFilter() {
        val findInclinedEdgesFilter = arrayOf(
            doubleArrayOf(-1.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, -2.0, 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 6.0, 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, -2.0, 0.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, -1.0),
        )
        val findInclinedEdgesFilteringInfo = FilteringInfo(findInclinedEdgesFilter, 1.0, 0.0)
        filterTest(findInclinedEdgesFilteringInfo, "find-inclined-edges")
    }

    @Test
    @DisplayName("All edges finding filter")
    fun findAllEdgesFilter() {
        val findAllEdgesFilter = arrayOf(
            doubleArrayOf(-1.0, -1.0, -1.0),
            doubleArrayOf(-1.0, 8.0, -1.0),
            doubleArrayOf(-1.0, -1.0, -1.0)
        )
        val findAllEdgesFilteringInfo = FilteringInfo(findAllEdgesFilter, 1.0, 0.0)
        filterTest(findAllEdgesFilteringInfo, "find-all-edges")
    }

    @Test
    @DisplayName("First Sharpen filter")
    fun firstSharpenFilter() {
        val firstSharpenFilter = arrayOf(
            doubleArrayOf(-1.0, -1.0, -1.0),
            doubleArrayOf(-1.0, 9.0, -1.0),
            doubleArrayOf(-1.0, -1.0, -1.0)
        )
        val firstSharpenFilteringInfo = FilteringInfo(firstSharpenFilter, 1.0, 0.0)
        filterTest(firstSharpenFilteringInfo, "first-sharpen")
    }

    @Test
    @DisplayName("Second Sharpen filter")
    fun secondSharpenFilter() {
        val secondSharpenFilter = arrayOf(
            doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0),
            doubleArrayOf(-1.0, 2.0, 2.0, 2.0, -1.0),
            doubleArrayOf(-1.0, 2.0, 8.0, 2.0, -1.0),
            doubleArrayOf(-1.0, 2.0, 2.0, 2.0, -1.0),
            doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0)
        )
        val secondSharpenFilteringInfo = FilteringInfo(secondSharpenFilter, 1.0 / 8.0, 0.0)
        filterTest(secondSharpenFilteringInfo, "second-sharpen")
    }

    @Test
    @DisplayName("Third Sharpen filter")
    fun thirdSharpenFilter() {
        val thirdSharpenFilter = arrayOf(
            doubleArrayOf(1.0, 1.0, 1.0),
            doubleArrayOf(1.0, -7.0, 1.0),
            doubleArrayOf(1.0, 1.0, 1.0)
        )
        val thirdSharpenFilteringInfo = FilteringInfo(thirdSharpenFilter, 1.0, 0.0)
        filterTest(thirdSharpenFilteringInfo, "third-sharpen")
    }

    @Test
    @DisplayName("Emboss 3x3 filter")
    fun emboss3x3Filter() {
        val emboss3x3Filter = arrayOf(
            doubleArrayOf(-1.0, -1.0, 0.0),
            doubleArrayOf(-1.0, 0.0, 1.0),
            doubleArrayOf(0.0, 1.0, 1.0)
        )
        val emboss3x3FilteringInfo = FilteringInfo(emboss3x3Filter, 1.0, 128.0)
        filterTest(emboss3x3FilteringInfo, "emboss-3x3")
    }

    @Test
    @DisplayName("Emboss 5x5 filter")
    fun emboss5x5Filter() {
        val emboss5x5Filter = arrayOf(
            doubleArrayOf(-1.0, -1.0, -1.0, -1.0, 0.0),
            doubleArrayOf(-1.0, -1.0, -1.0, 0.0, 1.0),
            doubleArrayOf(-1.0, -1.0, 0.0, 1.0, 1.0),
            doubleArrayOf(-1.0, 0.0, 1.0, 1.0, 1.0),
            doubleArrayOf(0.0, 1.0, 1.0, 1.0, 1.0)
        )
        val emboss5x5FilteringInfo = FilteringInfo(emboss5x5Filter, 1.0, 128.0)
        filterTest(emboss5x5FilteringInfo, "emboss-5x5")
    }

    @Test
    @DisplayName("Mean filter")
    fun meanFilter() {
        val meanFilter = arrayOf(
            doubleArrayOf(1.0, 1.0, 1.0),
            doubleArrayOf(1.0, 1.0, 1.0),
            doubleArrayOf(1.0, 1.0, 1.0)
        )
        val meanFilteringInfo = FilteringInfo(meanFilter, 1.0 / 9.0, 0.0)
        filterTest(meanFilteringInfo, "mean")
    }
}
