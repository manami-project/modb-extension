package io.github.manamiproject.modb.extension.rawdata

import io.github.manamiproject.modb.core.config.AnimeId

interface RawDataRetriever {

    suspend fun retrieveRawData(id: AnimeId): String
}