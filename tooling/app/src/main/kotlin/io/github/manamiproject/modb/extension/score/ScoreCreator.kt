package io.github.manamiproject.modb.extension.score

import java.net.URI

/**
 * Creates a [Score] for an anime.
 * @since 1.0.0
 */
interface ScoreCreator {

    /**
     * @since 1.0.0
     * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
     * @return Either the [Score] or [ScoreNoteFound] creating a score was not possible.
     */
    suspend fun createScore(sources: Collection<URI>): ScoreReturnValue
}