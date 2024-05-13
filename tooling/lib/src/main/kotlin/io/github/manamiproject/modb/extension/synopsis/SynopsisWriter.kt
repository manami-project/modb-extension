package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * @since 1.0.0
 */
public interface SynopsisWriter {

    /**
     * @since 1.0.0
     * @param sources
     * @param synopsis
     */
    public suspend fun saveOrUpdateSynopsis(sources: Collection<URI>, synopsis: Synopsis)

    /**
     * @since 1.0.0
     * @param anime
     * @param synopsis
     */
    public suspend fun saveOrUpdateSynopsis(anime: Anime, synopsis: Synopsis): Unit = saveOrUpdateSynopsis(anime.sources, synopsis)
}