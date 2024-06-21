package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.anilist.AnilistConfig
import io.github.manamiproject.modb.anilist.AnilistDefaultTokenRepository
import io.github.manamiproject.modb.anilist.AnilistDefaultTokenRetriever
import io.github.manamiproject.modb.anilist.AnilistDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import kotlinx.coroutines.runBlocking
import java.net.URI

/**
 * Loads the score from anilist.co
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider. **Default:** [AnilistConfig]
 * @property rawDataRetriever Handles the retrieval of raw data from the meta data provider so that the source doesn't matter for the caller.
 * @property extractor Extracts specific data from the raw data.
 */
class AnilistRawScoreLoader(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig = AnilistConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        metaDataProviderConfig = metaDataProviderConfig,
        downloader = AnilistDownloader(metaDataProviderConfig),
    ),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawScoreLoader {

    init {
        runBlocking {
            AnilistDefaultTokenRepository.token = AnilistDefaultTokenRetriever().retrieveToken()
        }
    }

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = metaDataProviderConfig.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = extractor.extract(content, mapOf(
            "score" to "$.data.Media.meanScore",
        ))

        if (data.notFound("score")) {
            return NoRawScore
        }

        val rawScore = data.double("score")

        return if (rawScore == 0.0) {
            NoRawScore
        } else {
            RawScore(rawScore, 1.0..100.0)
        }
    }
}