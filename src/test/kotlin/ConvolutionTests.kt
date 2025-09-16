import org.example.BMPHandler
import org.example.ConvolutionSolver
import org.example.FilteringInfo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import org.bytedeco.opencv.opencv_core.Mat

class ConvolutionTests {
    val solver = ConvolutionSolver()
    val handler = BMPHandler()

    fun checkEquality(bmp1: Mat, bmp2: Mat) {
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

    @Test
    @DisplayName("Convolution with ID filter doesn't change image")
    fun checkIDFilter() {
        val filter = arrayOf(doubleArrayOf(0.0, 0.0, 0.0), doubleArrayOf(0.0, 1.0, 0.0), doubleArrayOf(0.0, 0.0, 0.0))
        val filteringInfo = FilteringInfo(filter, 1.0, 0.0)
        val input = handler.readBMP("src/test/resources/in/example.bmp")
        val output = solver.convolve(input, filteringInfo)
        checkEquality(input, output)
    }

    @Test
    @DisplayName("Blur filter")
    fun checkBlurFilter() {
        val filter = arrayOf(doubleArrayOf(0.0, 0.2, 0.0), doubleArrayOf(0.2, 0.2, 0.2), doubleArrayOf(0.0, 0.2, 0.0))
        val filteringInfo = FilteringInfo(filter, 1.0, 0.0)
        val input = handler.readBMP("src/test/resources/in/example.bmp")
        val output = solver.convolve(input, filteringInfo)
        val reference = handler.readBMP("src/test/resources/ref_out/filterblur.bmp")
        checkEquality(reference, output)
    }
}
