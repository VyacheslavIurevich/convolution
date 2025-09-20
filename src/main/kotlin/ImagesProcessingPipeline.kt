import org.bytedeco.opencv.opencv_core.Mat
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ImagesProcessingPipeline(
    val solver: ConvolutionSolver = ConvolutionSolver(),
    private val handler: BMPHandler = BMPHandler(),
    maxQueueSize: Int = 10,
    private val readerThreadsNum: Int = 2,
    private val workerThreadsNum: Int = Runtime.getRuntime().availableProcessors() - 2,
    private val writerThreadsNum: Int = 2,
) {
    init {
        require(readerThreadsNum >= 1) { "Reader threads number must be not less than 1, got $readerThreadsNum" }
        require(workerThreadsNum >= 1) { "Worker threads number must be not less than 1, got $workerThreadsNum" }
        require(writerThreadsNum >= 1) { "Writer threads number must be not less than 1, got $writerThreadsNum" }
        require(maxQueueSize >= 1) { "Max queue size must be not less than 1, got $maxQueueSize" }
        val availableProcessorsNum = Runtime.getRuntime().availableProcessors()
        val usedThreadsNum = readerThreadsNum + workerThreadsNum + writerThreadsNum
        require(usedThreadsNum <= availableProcessorsNum) {
            "Threads number must be less or equal than $availableProcessorsNum, got $usedThreadsNum"
        }
    }

    private val readerQueue = LinkedBlockingQueue<Pair<ImageTask, Mat>>(maxQueueSize)
    private val writerQueue = LinkedBlockingQueue<Pair<ImageTask, Mat>>(maxQueueSize)
    private val executor = Executors.newFixedThreadPool(readerThreadsNum + workerThreadsNum + writerThreadsNum)
    private val processedCount = AtomicInteger(0)
    private val failedCount = AtomicInteger(0)
    private val pollTimeout = 100L
    private val awaitTerminationTimeout = 1L

    fun processImages(
        imageTasks: List<ImageTask>,
        onProgress: (processed: Int, total: Int, failed: Int) -> Unit,
    ) {
        val totalTasks = imageTasks.size

        for (threadID in 0..<readerThreadsNum) {
            executor.submit {
                var index = threadID
                while (index < totalTasks) {
                    try {
                        val task = imageTasks[index]
                        val image = handler.readBMP(task.input)
                        readerQueue.put(Pair(task, image))
                    } catch (e: Exception) {
                        println("Reader error: ${e.message}")
                        failedCount.incrementAndGet()
                    } finally {
                        index += readerThreadsNum
                    }
                }
            }
        }

        repeat(workerThreadsNum) {
            executor.submit {
                while (processedCount.get() < totalTasks) {
                    try {
                        val (task, image) =
                            readerQueue.poll(pollTimeout, TimeUnit.MILLISECONDS)
                                ?: continue
                        val processedImage = solver.convolve(image, task.filteringInfo, task.implementation)
                        writerQueue.put(Pair(task, processedImage))
                    } catch (_: InterruptedException) {
                        break
                    } catch (e: Exception) {
                        println("Worker error: ${e.message}")
                        failedCount.incrementAndGet()
                    }
                }
            }
        }

        repeat(writerThreadsNum) {
            executor.submit {
                while (processedCount.get() < totalTasks) {
                    try {
                        val (task, processedImage) =
                            writerQueue.poll(pollTimeout, TimeUnit.MILLISECONDS)
                                ?: continue
                        handler.writeBMP(task.output, processedImage)
                        val currentProcessed = processedCount.incrementAndGet()
                        onProgress(currentProcessed, totalTasks, failedCount.get())
                    } catch (_: InterruptedException) {
                        break
                    } catch (e: Exception) {
                        println("Writer error: ${e.message}")
                        failedCount.incrementAndGet()
                    }
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(awaitTerminationTimeout, TimeUnit.HOURS)
    }
}
