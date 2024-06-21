package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.livechart.LivechartConfig
import io.github.manamiproject.modb.livechart.LivechartDownloader
import java.net.URI

/**
 * Loads the score from livechart.me
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider. **Default:** [LivechartConfig]
 * @property rawDataRetriever Handles the retrieval of raw data from the meta data provider so that the source doesn't matter for the caller.
 * @property xmlExtractor  Uses XPath to extract data from HTML.
 * @property jsonExtractor Uses JsonPath to extract data from JSON.
 */
class LivechartRawScoreLoader(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig = LivechartConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        metaDataProviderConfig = metaDataProviderConfig,
        downloader = LivechartDownloader(metaDataProviderConfig),
    ),
    private val xmlExtractor: DataExtractor = XmlDataExtractor,
    private val jsonExtractor: DataExtractor = JsonDataExtractor,
): RawScoreLoader {

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = metaDataProviderConfig.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = xmlExtractor.extract(content, mapOf(
            "jsonld" to "//script[@type='application/ld+json']/node()",
        ))

        val jsonld = data.listNotNull<String>("jsonld").first()
        val jsonldData = jsonExtractor.extract(jsonld, mapOf(
            "score" to "$.aggregateRating.ratingValue",
            "worstRating" to "$.aggregateRating.worstRating",
            "bestRating" to "$.aggregateRating.bestRating",
        ))

        if (jsonldData.notFound("score")) {
            return NoRawScore
        }

        val rawScore = jsonldData.double("score")
        val from = jsonldData.doubleOrDefault("worstRating", 1.0)
        val to = jsonldData.doubleOrDefault("bestRating", 10.0)

        return if (rawScore == 0.0) {
            NoRawScore
        } else {
            RawScore(rawScore, from..to)
        }
    }
}