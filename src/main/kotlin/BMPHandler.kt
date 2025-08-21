package org.example

import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.global.opencv_imgcodecs.*

class BMPHandler {
    fun readBMP(input: String): Mat {
        require(input.endsWith(".bmp")) { "Input file must be .bmp, got $input" }
        val image: Mat? = imread(input)
        require(image != null) { "Image must be successfully read" }
        return image
    }

    fun writeBMP(output: String, image: Mat) {
        require(output.endsWith(".bmp")) { "Output file must be .bmp, got $output" }
        val result = imwrite(output, image)
        require(result) { "Write must be successful" }
    }
}
