package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * Adds a synopsis to an extension data file.
 * @since 1.0.0
 */
public interface SynopsisWriter {

    /**
     * Adds a score to an extension data file.
     * @since 1.0.0
     * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
     * @param synopsis [Synopsis] to save.
     */
    public suspend fun saveOrUpdateSynopsis(sources: Collection<URI>, synopsis: Synopsis)

    /**
     * Adds a score to an extension data file.
     * @since 1.0.0
     * @param anime Anime instance.
     * @param synopsis [Synopsis] to save.
     */
    public suspend fun saveOrUpdateSynopsis(anime: Anime, synopsis: Synopsis): Unit = saveOrUpdateSynopsis(anime.sources, synopsis)
}