package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.extensions.neitherNullNorBlank
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

/**
 * @since 1.0.0
 */
public sealed class ScoreReturnValue

/**
 * @since 1.0.0
 */
public data object ScoreNoteFound: ScoreReturnValue()

/**
 * @since 1.0.0
 * @param arithmeticMean
 * @param arithmeticGeometricMean
 * @param median
 * @param lastUpdate
 */
public data class Score(
    val arithmeticMean: Double = 0.0,
    val arithmeticGeometricMean: Double = 0.0,
    val median: Double = 0.0,
    private val lastUpdate: String = LocalDate.now().format(ISO_LOCAL_DATE),
): ScoreReturnValue() {

    init {
        require(YEAR_REGEX.matches(lastUpdate)) { "Property 'lastUpdate' must be set and match ISO_LOCAL_DATE format." }
        require(arithmeticMean in 0.0..10.0) { "Property 'arithmeticMean' must be within range 0.0 - 10.0." }
        require(arithmeticGeometricMean in 0.0..10.0) { "Property 'arithmeticGeometricMean' must be within range 0.0 - 10.0." }
        require(median in 0.0..10.0) { "Property 'median' must be within range 0.0 - 10.0." }
    }

    /**
     * @since 1.0.0
     */
    val lastUpdatedAt: LocalDate
        get() = LocalDate.parse(lastUpdate)

    private companion object {
        private val YEAR_REGEX = """^\d{4}-\d{2}-\d{2}$""".toRegex()
    }
}