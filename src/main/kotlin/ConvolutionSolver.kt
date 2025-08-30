package org.example

import org.bytedeco.opencv.opencv_core.*
import kotlin.math.max
import kotlin.math.min

class ConvolutionSolver {
    fun convolve(image: Mat, values: FilteringInfo): Mat {
        val filter = values.filter
        val filterHeight = values.rows
        val filterWidth = values.cols
        val w = image.cols()
        val h = image.rows()
        for (x in 0 until w) {
            for (y in 0 until h) {
                val pixel = doubleArrayOf(0.0, 0.0, 0.0) // pixel[0] is blue, pixel[1] is green, pixel[2] is red
                for (filterY in 0 until filterHeight) {
                    for (filterX in 0 until filterWidth) {
                        val imageX = (x - filterWidth / 2 + filterX + w) % w
                        val imageY = (y - filterHeight / 2 + filterY + h) % h
                        for (idx in 0..2) {
                            pixel[idx] += (image.ptr(imageY, imageX).get(idx.toLong())
                                .toInt() and 0xFF) * filter[filterY][filterX]
                        }
                    }
                }
                for (idx in 0..2) {
                    val resultColorValue = min(max(values.factor * pixel[idx] + values.bias, 0.0), 255.0)
                    image.ptr(y, x).put(idx.toLong(), resultColorValue.toInt().toByte())
                }
            }
        }
        return image
    }
}

