import org.bytedeco.opencv.opencv_core.Mat
import kotlin.test.assertEquals

fun checkBMPEquality(
    bmp1: Mat,
    bmp2: Mat,
) {
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
