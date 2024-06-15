package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.kitsu.KitsuConfig
import io.github.manamiproject.modb.kitsu.KitsuDownloader
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property rawDataRetriever
 * @property extractor
 */
class KitsuRawScoreLoader(
    private val appConfig: Config,
    private val config: MetaDataProviderConfig = KitsuConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        config = config,
        downloader = KitsuDownloader(config),
    ),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawScoreLoader {

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = config.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = extractor.extract(content, mapOf(
            "score" to "$.data.attributes.averageRating",
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