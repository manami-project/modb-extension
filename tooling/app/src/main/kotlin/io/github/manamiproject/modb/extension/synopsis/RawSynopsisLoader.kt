package io.github.manamiproject.modb.extension.synopsis

import java.net.URI

/**
 * Loads synopsis from the meta data providers.
 * @since 1.0.0
 */
interface RawSynopsisLoader {

    /**
     * Loads the score as presented on the meta data providers site.
     * @since 1.0.0
     * @param source [URI] identifying an anime on the meta data provider site. Basically the same URI you find in the "sources" array in anime-offline-database.
     * @return Either the value wrapped in [RawSynopsis] or [NoRawSynopsis].
     */
    suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue
}