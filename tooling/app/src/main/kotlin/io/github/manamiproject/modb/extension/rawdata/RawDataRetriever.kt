package io.github.manamiproject.modb.extension.rawdata

import io.github.manamiproject.modb.core.config.AnimeId

/**
 * Retrieves raw data from meta data providers.
 * @since 1.0.0
 */
interface RawDataRetriever {

    /**
     * Retrieves raw data for a specific anime.
     * @since 1.0.0
     * @param id Id of a specific anime.
     * @return Raw data. Can be HTML, JSON or any other data exchange format.
     */
    suspend fun retrieveRawData(id: AnimeId): String
}