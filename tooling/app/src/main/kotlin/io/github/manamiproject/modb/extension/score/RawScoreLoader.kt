package io.github.manamiproject.modb.extension.score

import java.net.URI

/**
 * Loads scores from the meta data providers.
 * @since 1.0.0
 */
interface RawScoreLoader {

    /**
     * Loads the score as presented on the meta data providers site.
     * @since 1.0.0
     * @param source [URI] identifying an anime on the meta data provider site. Basically the same URI you find in the "sources" array in anime-offline-database.
     * @return Either the value wrapped in [RawScore] or [NoRawScore].
     */
    suspend fun loadRawScore(source: URI): RawScoreReturnValue
}