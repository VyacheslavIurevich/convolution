import java.io.File

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        if (args[0] == "--bench") {
            runBenchmark()
        } else if (args[0] == "--benchpic") {
            runOnePicBench()
        }
    } else {
        runInteractive()
    }
}

fun runInteractive() {
    print("Enter input folder ")
    val inputFolder = File(readln())
    require(inputFolder.isDirectory) { "Input folder must be a directory" }

    print("Enter output folder ")
    val outputFolder = File(readln())
    if (!outputFolder.exists()) {
        val result = outputFolder.mkdirs()
        require(result) { "Output folder must be able to be created" }
    }
    require(outputFolder.isDirectory) { "Output folder must be a directory" }

    val inputFiles: Array<out File?>? = inputFolder.listFiles()
    require(inputFiles?.isNotEmpty() ?: false) { "Input folder must not be empty" }

    print("Write 0 for sequential pipeline, 1 for parallel ")
    val pipelineType = readln().toInt()
    require(pipelineType == 0 || pipelineType == 1) { "Pipeline type must be 0 or 1, got $pipelineType" }

    val solver = ConvolutionSolver()
    val filters = solver.availableFilters

    println("Available filters")
    for (filterName in filters.keys) println(filterName)
    print("Enter name of chosen filter ")
    val selectedFilter = readln()
    require(selectedFilter in filters.keys) { "Chosen filter must exist, got $selectedFilter" }
    val filteringInfo = filters[selectedFilter]
    require(filteringInfo != null) { "Filter must exist" }

    println("Select convolution implementation")
    for (implementation in ConvolutionImplementation.entries) println(implementation.name)
    print("Enter name of selected implementation ")
    val selectedImplementationName = readln()
    var selectedImplementation = ConvolutionImplementation.SEQUENTIAL
    for (implementation in ConvolutionImplementation.entries) {
        if (selectedImplementationName == implementation.name) {
            selectedImplementation = implementation
            break
        }
    }

    if (selectedImplementationName == "PARALLEL_BY_TILES") {
        print("Enter tile height ")
        val tileHeight = readln().toInt()
        print("Enter tile width ")
        val tileWidth = readln().toInt()
        solver.tileHeight = tileHeight
        solver.tileWidth = tileWidth
    }

    val tasks = mutableListOf<ImageTask>()
    for (inputFile in inputFiles) {
        require(inputFile != null) { "All input files must be readable" }
        val outputFile = File(outputFolder, inputFile.name)
        tasks.add(
            ImageTask(
                inputFile.absolutePath,
                outputFile.absolutePath,
                filteringInfo,
                selectedImplementation,
            ),
        )
    }

    if (pipelineType == 0) {
        val handler = BMPHandler()
        var processed = 0
        var failed = 0
        for (task in tasks) {
            try {
                val input = handler.readBMP(task.input)
                val output = solver.convolve(input, task.filteringInfo, task.implementation)
                handler.writeBMP(task.output, output)
                ++processed
            } catch (e: Exception) {
                ++failed
                println("Failed to process ${task.input}: ${e.message}")
            }
            println("Processed: $processed/${tasks.size}, failed: $failed")
        }
    } else {
        print("Enter max queue size ")
        val maxQueueSize = readln().toInt()
        println("Available threads number: ${Runtime.getRuntime().availableProcessors()}")
        print("Enter reader threads number ")
        val readerThreadsNum = readln().toInt()
        print("Enter worker threads number ")
        val workerThreadsNum = readln().toInt()
        print("Enter writer threads num ")
        val writerThreadsNum = readln().toInt()

        val pipeline =
            ImagesProcessingPipeline(
                maxQueueSize = maxQueueSize,
                readerThreadsNum = readerThreadsNum,
                workerThreadsNum = workerThreadsNum,
                writerThreadsNum = writerThreadsNum,
                solver = solver,
            )
        pipeline.processImages(tasks) { processed, total, failed ->
            println("Processed: $processed/$total, failed: $failed")
        }
    }
}

fun runOnePicBench() {
    val solver = ConvolutionSolver()
    val filters = solver.availableFilters
    val selectedFilter = "emboss-5x5"
    val filteringInfo = filters[selectedFilter]
    require(filteringInfo != null) { "Filter must exist" }
    solver.tileHeight = 32
    solver.tileWidth = 32
    val handler = BMPHandler()
    val input = handler.readBMP("src/test/resources/in/pics/big.bmp")
    for (implementation in ConvolutionImplementation.entries) {
        println(implementation.name)
        val startTime = System.currentTimeMillis()
        solver.convolve(input, filteringInfo, implementation)
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        println("Time taken: ${duration}ms")
    }
}

fun runBenchmark() {
    val inputFolder = File("src/test/resources/in/CALTECH-BMP-1500")
    val seqOutputFolder = File("src/test/resources/bench_output/seq")
    val parallelOutputFolder = File("src/test/resources/bench_output/parallel")

    val inputFiles: Array<out File?>? = inputFolder.listFiles()
    require(inputFiles?.isNotEmpty() ?: false) { "Input folder must not be empty" }

    val solver = ConvolutionSolver()

    val filters = solver.availableFilters
    val selectedFilter = "emboss-5x5"
    val filteringInfo = filters[selectedFilter]
    require(filteringInfo != null) { "Filter must exist" }

    val implementation = ConvolutionImplementation.SEQUENTIAL
    solver.tileHeight = 32
    solver.tileWidth = 32

    val seqTasks = mutableListOf<ImageTask>()
    for (inputFile in inputFiles) {
        require(inputFile != null) { "All input files must be readable" }
        val outputFile = File(seqOutputFolder, "${inputFile.name}")
        seqTasks.add(
            ImageTask(
                inputFile.absolutePath,
                outputFile.absolutePath,
                filteringInfo,
                implementation,
            ),
        )
    }

    println("Sequential pipeline")
    val seqStartTime = System.currentTimeMillis()
    val handler = BMPHandler()
    var processed = 0
    val total = seqTasks.size
    var failed = 0

    for (task in seqTasks) {
        try {
            val input = handler.readBMP(task.input)
            val output = solver.convolve(input, filteringInfo, ConvolutionImplementation.SEQUENTIAL)
            handler.writeBMP(task.output, output)
            ++processed
        } catch (e: Exception) {
            println("Error: ${e.message}")
            ++failed
        }
        println("Progress: $processed/$total, failed: $failed")
    }

    val seqEndTime = System.currentTimeMillis()
    val seqDuration = seqEndTime - seqStartTime

    println("Parallel pipeline")

    val pipeline =
        ImagesProcessingPipeline(
            maxQueueSize = 10,
            readerThreadsNum = 2,
            workerThreadsNum = Runtime.getRuntime().availableProcessors() - 4,
            writerThreadsNum = 2,
            solver = solver,
        )

    val pipelineTasks = mutableListOf<ImageTask>()
    for (inputFile in inputFiles) {
        require(inputFile != null) { "All input files must be readable" }
        val outputFile = File(parallelOutputFolder, "${inputFile.name}")
        pipelineTasks.add(
            ImageTask(
                inputFile.absolutePath,
                outputFile.absolutePath,
                filteringInfo,
                implementation,
            ),
        )
    }

    val parallelStartTime = System.currentTimeMillis()
    pipeline.processImages(pipelineTasks) { processed, total, failed ->
        println("Progress: $processed/$total, failed: $failed")
    }
    val parallelEndTime = System.currentTimeMillis()
    val parallelDuration = parallelEndTime - parallelStartTime
    println("Time taken by sequential pipeline: ${seqDuration}ms")
    println("Time taken by parallel pipeline: ${parallelDuration}ms")
}
