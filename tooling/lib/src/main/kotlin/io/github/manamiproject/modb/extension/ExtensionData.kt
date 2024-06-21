package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.extension.score.Score
import io.github.manamiproject.modb.extension.score.ScoreNotFound
import io.github.manamiproject.modb.extension.score.ScoreReturnValue
import io.github.manamiproject.modb.extension.synopsis.Synopsis
import io.github.manamiproject.modb.extension.synopsis.SynopsisNotFound
import io.github.manamiproject.modb.extension.synopsis.SynopsisReturnValue
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

/**
 * @since 1.0.0
 */
public sealed class ExtensionDataReturnValue

/**
 * @since 1.0.0
 */
public data object ExtensionDataNotFound: ExtensionDataReturnValue()

/**
 * @since 1.0.0
 * @property sources
 * @property synopsis
 * @property score
 */
public data class ExtensionData(
    val sources: List<URI>,
    private val synopsis: Synopsis? = null,
    private val score: Score? = null,
    private val lastUpdate: String = LocalDate.now().format(ISO_LOCAL_DATE),
): ExtensionDataReturnValue() {

    init {
        require(sources.isNotEmpty()) { "Sources must not be empty" }
        require(YEAR_REGEX.matches(lastUpdate)) { "Property 'lastUpdate' must be set and match ISO_LOCAL_DATE format." }
    }

    /**
     * Last update as [LocalDate].
     * @since 1.0.0
     */
    val lastUpdatedAt: LocalDate
        get() = LocalDate.parse(lastUpdate)

    /**
     * @since 1.0.0
     * @return
     */
    public fun synopsis(): SynopsisReturnValue {
        return when {
            synopsis == null -> SynopsisNotFound
            synopsis.text.eitherNullOrBlank() || synopsis.author.eitherNullOrBlank() -> SynopsisNotFound
            else -> synopsis
        }
    }

    /**
     * @since 1.0.0
     * @return
     */
    public fun score(): ScoreReturnValue {
        return when {
            score == null -> ScoreNotFound
            score.arithmeticMean == 0.0 && score.arithmeticGeometricMean == 0.0 && score.median == 0.0 -> ScoreNotFound
            else -> score
        }
    }

    private companion object {
        private val YEAR_REGEX = """^\d{4}-\d{2}-\d{2}$""".toRegex()
    }
}

