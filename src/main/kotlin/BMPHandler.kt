package org.example

import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.global.opencv_imgcodecs.*

class BMPHandler {
    fun readBMP(input: String): Mat {
        require(input.endsWith(".bmp"))
        val image: Mat? = imread(input)
        require(image != null)
        return image
    }

    fun writeBMP(output: String, image: Mat) {
        require(output.endsWith(".bmp"))
        val result = imwrite(output, image)
        require(result)
    }
}
