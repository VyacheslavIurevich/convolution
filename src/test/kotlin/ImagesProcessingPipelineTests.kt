import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ImagesProcessingPipelineTests {
    private val availableProcessorsNum = Runtime.getRuntime().availableProcessors()

    @Test
    @DisplayName("Creation of ImagesProcessingPipeline with 0 reader threads fails with exception")
    fun checkCreationOfPipelineWithoutReaderThreads() {
        assertThrows<IllegalArgumentException> {
            ImagesProcessingPipeline(readerThreadsNum = 0)
        }
    }

    @Test
    @DisplayName("Creation of ImagesProcessingPipeline with 0 worker threads fails with exception")
    fun checkCreationOfPipelineWithoutWorkerThreads() {
        assertThrows<IllegalArgumentException> {
            ImagesProcessingPipeline(workerThreadsNum = 0)
        }
    }

    @Test
    @DisplayName("Creation of ImagesProcessingPipeline with 0 writer threads fails with exception")
    fun checkCreationOfPipelineWithoutWriterThreads() {
        assertThrows<IllegalArgumentException> {
            ImagesProcessingPipeline(writerThreadsNum = 0)
        }
    }

    @Test
    @DisplayName("Creation of ImagesProcessingPipeline with 0 maximum queue size fails with exception")
    fun checkCreationOfPipelineWithEmptyQueue() {
        assertThrows<IllegalArgumentException> {
            ImagesProcessingPipeline(maxQueueSize = 0)
        }
    }

    @Test
    @DisplayName("Creation of ImagesProcessingPipeline with too much threads fails with exception")
    fun checkCreationOfPipelineWithTooMuchThreads() {
        assertThrows<IllegalArgumentException> {
            ImagesProcessingPipeline(
                readerThreadsNum = availableProcessorsNum,
                workerThreadsNum = availableProcessorsNum,
                writerThreadsNum = availableProcessorsNum,
            )
        }
    }
}
