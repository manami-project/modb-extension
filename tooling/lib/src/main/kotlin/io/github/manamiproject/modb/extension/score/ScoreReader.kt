package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.models.Anime
import java.net.URI

/**
 * @since 1.0.0
 */
public interface ScoreReader {

    /**
     * @since 1.0.0
     * @param sources
     * @return
     */
    public suspend fun findScore(sources: Collection<URI>): ScoreReturnValue

    /**
     * @since 1.0.0
     * @param anime
     * @return
     */
    public suspend fun findScore(anime: Anime): ScoreReturnValue = findScore(anime.sources)
}