package io.github.manamiproject.modb.extension.score

/**
 * @since 1.0.0
 */
sealed class RawScoreReturnValue

/**
 * @since 1.0.0
 */
data object NoRawScore: RawScoreReturnValue()

/**
 * @since 1.0.0
 * @param value
 * @param originalRange
 */
data class RawScore(private val value: Double, private val originalRange: ClosedFloatingPointRange<Double>) : RawScoreReturnValue() {

    /**
     * @since 1.0.0
     * @return
     */
    fun scaledValue(): Double = rescale(value, originalRange, 1.0..10.0)

    private fun rescale(value: Double, originalRange: ClosedFloatingPointRange<Double>, newRange: ClosedFloatingPointRange<Double>): Double {
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