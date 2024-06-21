package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * Adds a score to an extension data file.
 * @since 1.0.0
 */
public interface ScoreWriter {

    /**
     * Adds a score to an extension data file.
     * @since 1.0.0
     * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
     * @param score [Score] to save.
     */
    public suspend fun saveOrUpdateScore(sources: Collection<URI>, score: Score)

    /**
     * Adds a score to an extension data file.
     * @since 1.0.0
     * @param anime Anime instance.
     * @param score [Score] to save.
     */
    public suspend fun saveOrUpdateScore(anime: Anime, score: Score): Unit = saveOrUpdateScore(anime.sources, score)
}