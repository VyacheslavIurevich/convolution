import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FilteringInfoTests {
    @Test
    @DisplayName("Creation of FilteringInfo with empty filter matrix fails with exception")
    fun checkCreationFilteringInfoWithEmptyMatrix() {
        assertThrows<NoSuchElementException> {
            FilteringInfo(arrayOf(), 1.0, 0.0)
        }
    }

    @Test
    @DisplayName("Creation of FilteringInfo with empty row of filter matrix fails with exception")
    fun checkCreationFilteringInfoWithEmptyRowOfMatrix() {
        assertThrows<IllegalArgumentException> {
            FilteringInfo(
                arrayOf(
                    doubleArrayOf(0.0, 0.0, 0.0),
                    doubleArrayOf(),
                    doubleArrayOf(0.0, 0.0, 0.0),
                ),
                1.0,
                0.0,
            )
        }
    }

    @Test
    @DisplayName("Creation of FilteringInfo with even number of rows fails with exception")
    fun checkCreationFilteringInfoWithEvenNumberOfRows() {
        assertThrows<IllegalArgumentException> {
            FilteringInfo(
                arrayOf(
                    doubleArrayOf(0.0, 0.0, 0.0),
                    doubleArrayOf(0.0, 0.0, 0.0),
                ),
                1.0,
                0.0,
            )
        }
    }

    @Test
    @DisplayName("Creation of FilteringInfo with even number of columns fails with exception")
    fun checkCreationFilteringInfoWithEvenNumberOfCols() {
        assertThrows<IllegalArgumentException> {
            FilteringInfo(
                arrayOf(
                    doubleArrayOf(0.0, 0.0),
                    doubleArrayOf(0.0, 0.0),
                ),
                1.0,
                0.0,
            )
        }
    }

    @Test
    @DisplayName("Creation of FilteringInfo with non-matrix array filter fails with exception")
    fun checkCreationFilteringInfoWithNonMatrixArrayFilter() {
        assertThrows<IllegalArgumentException> {
            FilteringInfo(
                arrayOf(
                    doubleArrayOf(0.0, 0.0, 0.0),
                    doubleArrayOf(0.0, 0.0),
                    doubleArrayOf(0.0, 0.0, 0.0),
                ),
                1.0,
                0.0,
            )
        }
    }
}
