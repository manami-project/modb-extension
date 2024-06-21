package io.github.manamiproject.modb.extension.score

/**
 * @since 1.0.0
 */
sealed class RawScoreReturnValue

/**
 * Indicates that no score has been found.
 * @since 1.0.0
 */
data object NoRawScore: RawScoreReturnValue()

/**
 * Represents the score as it is found on the site of the meta data provider.
 * @since 1.0.0
 * @param value Score as-is
 * @param originalRange The range in which scores can be represented on the meta data provider site.
 */
data class RawScore(
    private val value: Double,
    private val originalRange: ClosedFloatingPointRange<Double>,
) : RawScoreReturnValue() {

    /**
     * Returns the original value rescaled to a score system from 1 to 10.
     * @since 1.0.0
     * @return A value between 1 to 10 representing the score of an anime where 1 is the worst rating and 10 the best.
     */
    fun scaledValue(): Double = rescale(value, originalRange, 1.0..10.0)

    private fun rescale(
        value: Double,
        originalRange: ClosedFloatingPointRange<Double>,
        newRange: ClosedFloatingPointRange<Double>,
    ): Double {
        if (originalRange == newRange) {
            return value
        }

        val newMin = newRange.start
        val newMax = newRange.endInclusive

        val minValue = originalRange.start
        val maxValue = originalRange.endInclusive

        return ( (value - minValue) / (maxValue - minValue) ) * (newMax - newMin) + newMin
    }
}