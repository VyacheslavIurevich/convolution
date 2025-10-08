class FilteringInfo(
    val filter: Array<DoubleArray>,
    val factor: Double,
    val bias: Double,
) {
    val rows = filter.size
    val cols = filter.first().size

    init {
        require(filter.all { it.isNotEmpty() }) { "All filter rows must be non-empty" }
        require(rows % 2 == 1) { "Filter must have an odd number of rows, got $rows" }
        require(cols % 2 == 1) { "Filter must have an odd number of columns, got $cols" }
        require(filter.all { it.size == cols }) { "All filter rows must have length equal $cols" }
    }
}
