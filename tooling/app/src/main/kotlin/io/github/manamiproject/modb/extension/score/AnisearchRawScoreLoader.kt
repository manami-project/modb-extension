package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.anisearch.AnisearchConfig
import io.github.manamiproject.modb.anisearch.AnisearchDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.remove
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property rawDataRetriever
 * @property xmlExtractor
 * @property jsonExtractor
 */
class AnisearchRawScoreLoader(
    private val appConfig: Config,
    private val config: MetaDataProviderConfig = AnisearchConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        config = config,
        downloader = AnisearchDownloader(config),
    ),
    private val xmlExtractor: DataExtractor = XmlDataExtractor,
    private val jsonExtractor: DataExtractor = JsonDataExtractor,
): RawScoreLoader {

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = config.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = xmlExtractor.extract(content, mapOf(
            "jsonld" to "//script[@type='application/ld+json']/node()",
            "score" to "//td[contains(text(), 'Calculated Value')]/text()",
        ))

        val jsonld = data.listNotNull<String>("jsonld").first()
        val jsonData = jsonExtractor.extract(jsonld, mapOf(
            "ratingValue" to "$.ratingValue",
            "worstRating" to "$.worstRating",
            "bestRating" to "$.bestRating",
        ))

        val from = jsonData.doubleOrDefault("worstRating", 0.1)
        val to = jsonData.doubleOrDefault("bestRating", 5.0)
        val rawScore = if (jsonData.notFound("ratingValue")) {
            data.string("score")
                .remove("Calculated Value")
                .substringBefore('=')
                .trim()
                .toDoubleOrNull() ?: 0.0
        } else {
            jsonData.doubleOrDefault("ratingValue")
        }

        return if (rawScore == 0.0) {
            NoRawScore
        } else {
            RawScore(rawScore, from..to)
        }
    }
}