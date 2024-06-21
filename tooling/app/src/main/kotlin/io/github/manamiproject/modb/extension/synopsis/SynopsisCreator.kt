package io.github.manamiproject.modb.extension.synopsis

import java.net.URI

/**
 * Creates a [Synopsis] for an anime.
 * @since 1.0.0
 */
interface SynopsisCreator {

    /**
     * Creates a synopsis for an anime.
     * @since 1.0.0
     * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
     * @return Either the [Synopsis] or [SynopsisNotFound] creating a score was not possible.
     */
    suspend fun createSynopsis(sources: Collection<URI>): SynopsisReturnValue
}