package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * Find a synopsis for an anime.
 * @since 1.0.0
 */
public interface SynopsisReader {

    /**
     * Find a synopsis for an anime.
     * @since 1.0.0
     * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
     * @return Either the [Synopsis] or [SynopsisNotFound] creating a score was not possible.
     */
    public suspend fun findSynopsis(sources: Collection<URI>): SynopsisReturnValue

    /**
     * Find a synopsis for an anime.
     * @since 1.0.0
     * @param anime Anime instance.
     * @return Either the [Synopsis] or [SynopsisNotFound] creating a score was not possible.
     */
    public suspend fun findSynopsis(anime: Anime): SynopsisReturnValue = findSynopsis(anime.sources)
}