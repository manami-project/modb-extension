package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.extension.score.Score
import io.github.manamiproject.modb.extension.score.ScoreNotFound
import io.github.manamiproject.modb.extension.score.ScoreReturnValue
import io.github.manamiproject.modb.extension.synopsis.Synopsis
import io.github.manamiproject.modb.extension.synopsis.SynopsisNotFound
import io.github.manamiproject.modb.extension.synopsis.SynopsisReturnValue
import java.net.URI

/**
 * Return for trying to retrieve an ExtensionData..
 * @since 1.0.0
 */
public sealed class ExtensionDataReturnValue

/**
 * Indicates that no ExtensionData has been found.
 * @since 1.0.0
 */
public data object ExtensionDataNotFound: ExtensionDataReturnValue()

/**
 * Whole data set for a single anime.
 * @since 1.0.0
 * @property sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
 * @property synopsis Descriptive text of an anime.
 * @property score Different score types creating an average across different meta data providers.
 */
public data class ExtensionData(
    val sources: List<URI>,
    private val synopsis: Synopsis? = null,
    private val score: Score? = null,
): ExtensionDataReturnValue() {

    init {
        require(sources.isNotEmpty()) { "Sources must not be empty" }
    }

    /**
     * Synopsis
     * @since 1.0.0
     * @return Either the [Synopsis] or [SynopsisNotFound] if a synopsis doesn't exist.
     */
    public fun synopsis(): SynopsisReturnValue {
        return when {
            synopsis == null -> SynopsisNotFound
            synopsis.text.eitherNullOrBlank() || synopsis.author.eitherNullOrBlank() -> SynopsisNotFound
            else -> synopsis
        }
    }

    /**
     * Score
     * @since 1.0.0
     * @return Either the [Score] or [ScoreNotFound] if a score doesn't exist.
     */
    public fun score(): ScoreReturnValue {
        return when {
            score == null -> ScoreNotFound
            score.arithmeticMean == 0.0 && score.arithmeticGeometricMean == 0.0 && score.median == 0.0 -> ScoreNotFound
            else -> score
        }
    }
}

