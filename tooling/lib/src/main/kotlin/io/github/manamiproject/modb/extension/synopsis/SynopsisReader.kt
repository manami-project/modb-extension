package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * @since 1.0.0
 */
public interface SynopsisReader {

    /**
     * @since 1.0.0
     * @param sources
     * @return
     */
    public suspend fun findSynopsis(sources: Collection<URI>): SynopsisReturnValue

    /**
     * @since 1.0.0
     * @param anime
     * @return
     */
    public suspend fun findSynopsis(anime: Anime): SynopsisReturnValue = findSynopsis(anime.sources)
}