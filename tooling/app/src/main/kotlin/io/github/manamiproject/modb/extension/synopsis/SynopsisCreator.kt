package io.github.manamiproject.modb.extension.synopsis

import java.net.URI

/**
 * @since 1.0.0
 */
interface SynopsisCreator {

    /**
     * @since 1.0.0
     * @param sources
     * @return
     */
    suspend fun createSynopsis(sources: Collection<URI>): SynopsisReturnValue
}