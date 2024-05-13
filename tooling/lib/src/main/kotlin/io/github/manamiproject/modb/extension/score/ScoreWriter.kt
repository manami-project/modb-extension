package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * @since 1.0.0
 */
public interface ScoreWriter {

    /**
     * @since 1.0.0
     * @param sources
     * @param score
     */
    public suspend fun saveOrUpdateScore(sources: Collection<URI>, score: Score)

    /**
     * @since 1.0.0
     * @param anime
     * @param score
     */
    public suspend fun saveOrUpdateScore(anime: Anime, score: Score): Unit = saveOrUpdateScore(anime.sources, score)
}