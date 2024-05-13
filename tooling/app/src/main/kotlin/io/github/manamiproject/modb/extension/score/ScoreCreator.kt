package io.github.manamiproject.modb.extension.score

import java.net.URI

/**
 * @since 1.0.0
 */
interface ScoreCreator {

    /**
     * @since 1.0.0
     * @param sources
     * @return
     */
    suspend fun createScore(sources: Collection<URI>): ScoreReturnValue
}