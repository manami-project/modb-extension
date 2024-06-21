package io.github.manamiproject.modb.extension.score

/**
 * Return for trying to retrieve a score.
 * @since 1.0.0
 */
public sealed class ScoreReturnValue

/**
 * Indicates that no score has been found.
 * @since 1.0.0
 */
public data object ScoreNotFound: ScoreReturnValue()

/**
 * Aggregated score across all available meta data providers.
 * @since 1.0.0
 * @param arithmeticMean aithmetic mean
 * @param arithmeticGeometricMean arithmetic-geometric-mean
 * @param median median
 */
public data class Score(
    val arithmeticMean: Double = 0.0,
    val arithmeticGeometricMean: Double = 0.0,
    val median: Double = 0.0,
): ScoreReturnValue() {

    init {
        require(arithmeticMean in 0.0..10.0) { "Property 'arithmeticMean' must be within range 0.0 - 10.0." }
        require(arithmeticGeometricMean in 0.0..10.0) { "Property 'arithmeticGeometricMean' must be within range 0.0 - 10.0." }
        require(median in 0.0..10.0) { "Property 'median' must be within range 0.0 - 10.0." }
    }
}