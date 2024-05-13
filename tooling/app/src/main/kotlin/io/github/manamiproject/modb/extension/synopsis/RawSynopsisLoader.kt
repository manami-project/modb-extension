package io.github.manamiproject.modb.extension.synopsis

import java.net.URI

/**
 * @since 1.0.0
 */
interface RawSynopsisLoader {

    /**
     * @since 1.0.0
     * @param source
     * @return
     */
    suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue
}