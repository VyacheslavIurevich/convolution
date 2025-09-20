import org.bytedeco.opencv.opencv_core.Mat
import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.min

class ConvolutionSolver {
    var tileHeight = 32
    var tileWidth = 32
    val availableFilters =
        mapOf(
            "id" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 1.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0),
                    ),
                    1.0,
                    0.0,
                ),
            "blur-3x3" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(0.0, 0.2, 0.0),
                        doubleArrayOf(0.2, 0.2, 0.2),
                        doubleArrayOf(0.0, 0.2, 0.0),
                    ),
                    1.0,
                    0.0,
                ),
            "blur-5x5" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0),
                        doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0),
                        doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0),
                    ),
                    1.0 / 13.0,
                    0.0,
                ),
            "gaussian-blur-3x3" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(1.0, 2.0, 1.0),
                        doubleArrayOf(2.0, 4.0, 2.0),
                        doubleArrayOf(1.0, 2.0, 1.0),
                    ),
                    1.0 / 16.0,
                    0.0,
                ),
            "gaussian-blur-5x5" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(1.0, 4.0, 6.0, 4.0, 1.0),
                        doubleArrayOf(4.0, 16.0, 24.0, 16.0, 4.0),
                        doubleArrayOf(6.0, 24.0, 36.0, 24.0, 6.0),
                        doubleArrayOf(4.0, 16.0, 24.0, 16.0, 4.0),
                        doubleArrayOf(1.0, 4.0, 6.0, 4.0, 1.0),
                    ),
                    1.0 / 256.0,
                    0.0,
                ),
            "motion-blur" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                    ),
                    1.0 / 9.0,
                    0.0,
                ),
            "find-horizontal-edges" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 2.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0),
                    ),
                    1.0,
                    0.0,
                ),
            "find-vertical-edges" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 4.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, -1.0, 0.0, 0.0),
                    ),
                    1.0,
                    0.0,
                ),
            "find-inclined-edges" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(-1.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, -2.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 6.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, -2.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, -1.0),
                    ),
                    1.0,
                    0.0,
                ),
            "find-all-edges" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(-1.0, -1.0, -1.0),
                        doubleArrayOf(-1.0, 8.0, -1.0),
                        doubleArrayOf(-1.0, -1.0, -1.0),
                    ),
                    1.0,
                    0.0,
                ),
            "first-sharpen" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(-1.0, -1.0, -1.0),
                        doubleArrayOf(-1.0, 9.0, -1.0),
                        doubleArrayOf(-1.0, -1.0, -1.0),
                    ),
                    1.0,
                    0.0,
                ),
            "second-sharpen" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0),
                        doubleArrayOf(-1.0, 2.0, 2.0, 2.0, -1.0),
                        doubleArrayOf(-1.0, 2.0, 8.0, 2.0, -1.0),
                        doubleArrayOf(-1.0, 2.0, 2.0, 2.0, -1.0),
                        doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0),
                    ),
                    1.0 / 8.0,
                    0.0,
                ),
            "third-sharpen" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(1.0, 1.0, 1.0),
                        doubleArrayOf(1.0, -7.0, 1.0),
                        doubleArrayOf(1.0, 1.0, 1.0),
                    ),
                    1.0,
                    0.0,
                ),
            "emboss-3x3" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(-1.0, -1.0, 0.0),
                        doubleArrayOf(-1.0, 0.0, 1.0),
                        doubleArrayOf(0.0, 1.0, 1.0),
                    ),
                    1.0,
                    128.0,
                ),
            "emboss-5x5" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(-1.0, -1.0, -1.0, -1.0, 0.0),
                        doubleArrayOf(-1.0, -1.0, -1.0, 0.0, 1.0),
                        doubleArrayOf(-1.0, -1.0, 0.0, 1.0, 1.0),
                        doubleArrayOf(-1.0, 0.0, 1.0, 1.0, 1.0),
                        doubleArrayOf(0.0, 1.0, 1.0, 1.0, 1.0),
                    ),
                    1.0,
                    128.0,
                ),
            "mean" to
                FilteringInfo(
                    arrayOf(
                        doubleArrayOf(1.0, 1.0, 1.0),
                        doubleArrayOf(1.0, 1.0, 1.0),
                        doubleArrayOf(1.0, 1.0, 1.0),
                    ),
                    1.0 / 9.0,
                    0.0,
                ),
        )

    private fun processPixel(
        image: Mat,
        filteringInfo: FilteringInfo,
        result: Mat,
        x: Int,
        y: Int,
    ) {
        val filter = filteringInfo.filter
        val filterHeight = filteringInfo.rows
        val filterWidth = filteringInfo.cols
        val w = image.cols()
        val h = image.rows()
        val pixel = doubleArrayOf(0.0, 0.0, 0.0)

        for (filterY in 0 until filterHeight) {
            for (filterX in 0 until filterWidth) {
                val imageX = (x - filterWidth / 2 + filterX + w) % w
                val imageY = (y - filterHeight / 2 + filterY + h) % h

                for (idx in 0..2) {
                    pixel[idx] += (image.ptr(imageY, imageX).get(idx.toLong()).toInt() and 0xFF) * filter[filterY][filterX]
                }
            }
        }

        for (idx in 0..2) {
            val resultColorValue = min(max(filteringInfo.factor * pixel[idx] + filteringInfo.bias, 0.0), 255.0)
            result.ptr(y, x).put(idx.toLong(), resultColorValue.toInt().toByte())
        }
    }

    fun convolve(
        image: Mat,
        filteringInfo: FilteringInfo,
        implementation: ConvolutionImplementation,
    ): Mat {
        val w = image.cols()
        val h = image.rows()
        val result = image.clone()

        when (implementation) {
            ConvolutionImplementation.SEQUENTIAL -> {
                IntStream.range(0, h).forEach { y ->
                    for (x in 0 until w) {
                        processPixel(image, filteringInfo, result, x, y)
                    }
                }
            }

            ConvolutionImplementation.PARALLEL_BY_ROWS -> {
                IntStream.range(0, h).parallel().forEach { y ->
                    for (x in 0 until w) {
                        processPixel(image, filteringInfo, result, x, y)
                    }
                }
            }

            ConvolutionImplementation.PARALLEL_BY_COLS -> {
                IntStream.range(0, w).parallel().forEach { x ->
                    for (y in 0 until h) {
                        processPixel(image, filteringInfo, result, x, y)
                    }
                }
            }

            ConvolutionImplementation.PARALLEL_BY_PIXELS -> {
                IntStream.range(0, h).parallel().forEach { y ->
                    IntStream.range(0, w).parallel().forEach { x ->
                        processPixel(image, filteringInfo, result, x, y)
                    }
                }
            }

            ConvolutionImplementation.PARALLEL_BY_TILES -> {
                require(tileHeight > 0) { "Tile height must be greater than 0, got $tileHeight" }
                require(tileWidth > 0) { "Tile width must be greater than 0, got $tileWidth" }

                val numTilesY = (h + tileHeight - 1) / tileHeight
                val numTilesX = (w + tileWidth - 1) / tileWidth

                IntStream.range(0, numTilesY * numTilesX).parallel().forEach { tileIndex ->
                    val tileY = tileIndex / numTilesX
                    val tileX = tileIndex % numTilesX

                    val startX = tileX * tileWidth
                    val startY = tileY * tileHeight
                    val endX = min(startX + tileWidth, w)
                    val endY = min(startY + tileHeight, h)

                    for (y in startY until endY) {
                        for (x in startX until endX) {
                            processPixel(image, filteringInfo, result, x, y)
                        }
                    }
                }
            }
        }

        return result
    }
}
