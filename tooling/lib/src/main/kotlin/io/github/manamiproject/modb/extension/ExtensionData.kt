package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.extension.score.Score
import io.github.manamiproject.modb.extension.score.ScoreNoteFound
import io.github.manamiproject.modb.extension.score.ScoreReturnValue
import io.github.manamiproject.modb.extension.synopsis.Synopsis
import io.github.manamiproject.modb.extension.synopsis.SynopsisNotFound
import io.github.manamiproject.modb.extension.synopsis.SynopsisReturnValue
import java.net.URI

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
): ExtensionDataReturnValue() {

    init {
        require(sources.isNotEmpty()) { "Sources must not be empty" }
    }

    /**
     * @since 1.0.0
     * @return
     */
    public fun synopsis(): SynopsisReturnValue {
        return when {
            synopsis == null -> SynopsisNotFound
            synopsis.text.isBlank() || synopsis.author.isBlank() -> SynopsisNotFound
            else -> synopsis
        }
    }

    /**
     * @since 1.0.0
     * @return
     */
    public fun score(): ScoreReturnValue {
        return when {
            score == null -> ScoreNoteFound
            score.arithmeticMean == 0.0 && score.arithmeticGeometricMean == 0.0 && score.median == 0.0 -> ScoreNoteFound
            else -> score
        }
    }
}

