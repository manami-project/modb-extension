package io.github.manamiproject.modb.extension.score

import java.net.URI

/**
 * @since 1.0.0
 */
interface RawScoreLoader {

    /**
     * @since 1.0.0
     * @param source
     * @return
     */
    suspend fun loadRawScore(source: URI): RawScoreReturnValue
}