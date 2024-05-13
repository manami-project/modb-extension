package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.animeplanet.AnimePlanetConfig
import io.github.manamiproject.modb.animeplanet.AnimePlanetDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property xmlExtractor
 * @property jsonExtractor
 */
class AnimePlanetRawScoreLoader(
    private val config: MetaDataProviderConfig = AnimePlanetConfig,
    private val downloader: Downloader = AnimePlanetDownloader(config),
    private val xmlExtractor: DataExtractor = XmlDataExtractor,
    private val jsonExtractor: DataExtractor = JsonDataExtractor,
): RawScoreLoader {

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
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