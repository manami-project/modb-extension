package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * Find score for an anime.
 * @since 1.0.0
 */
public interface ScoreReader {

    /**
     * Find a score for an anime.
     * @since 1.0.0
     * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
     * @return Either the [Score] or [ScoreNotFound] creating a score was not possible.
     */
    public suspend fun findScore(sources: Collection<URI>): ScoreReturnValue

    /**
     * Find a score for an anime.
     * @since 1.0.0
     * @param anime Anime instance.
     * @return Either the [Score] or [ScoreNotFound] creating a score was not possible.
     */
    public suspend fun findScore(anime: Anime): ScoreReturnValue = findScore(anime.sources)
}