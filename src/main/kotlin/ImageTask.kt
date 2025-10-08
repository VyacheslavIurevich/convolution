import java.io.File

class ImageTask(
    val input: String,
    val output: String,
    val filteringInfo: FilteringInfo,
    val implementation: ConvolutionImplementation,
) {
    init {
        require(input.endsWith(".bmp")) { "Input file must be .bmp, got $input" }
        require(output.endsWith(".bmp")) { "Output file must be .bmp, got $output" }
        require(File(input).let { it.exists() && it.isFile }) {
            "Input file must exist: $input"
        }
    }
}
