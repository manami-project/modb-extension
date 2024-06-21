package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.anidb.AnidbConfig
import io.github.manamiproject.modb.animeplanet.AnimePlanetConfig
import io.github.manamiproject.modb.animeplanet.AnimePlanetDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import java.net.URI

/**
 * Loads the score from anime-planet.com
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider. **Default:** [AnidbConfig]
 * @property rawDataRetriever Handles the retrieval of raw data from the meta data provider so that the source doesn't matter for the caller.
 * @property xmlExtractor  Uses XPath to extract data from HTML.
 * @property jsonExtractor Uses JsonPath to extract data from JSON.
 */
class AnimePlanetRawScoreLoader(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig = AnimePlanetConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        metaDataProviderConfig = metaDataProviderConfig,
        downloader = AnimePlanetDownloader(metaDataProviderConfig),
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

        val jsonld = data.string("jsonld")
        val jsonldData = jsonExtractor.extract(jsonld, mapOf(
            "worstRating" to "$.aggregateRating.worstRating",
            "bestRating" to "$.aggregateRating.bestRating",
            "ratingValue" to "$.aggregateRating.ratingValue",
        ))

        if (jsonldData.notFound("ratingValue")) {
            return NoRawScore
        }

        val rawScore = jsonldData.double("ratingValue")
        val from = jsonldData.doubleOrDefault("worstRating", 0.5)
        val to = jsonldData.doubleOrDefault("bestRating", 5.0)

        return if (rawScore == 0.0) {
            NoRawScore
        } else {
            RawScore(rawScore, from..to)
        }
    }
}