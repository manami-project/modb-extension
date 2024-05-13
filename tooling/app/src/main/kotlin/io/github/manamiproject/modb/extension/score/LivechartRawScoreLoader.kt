package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.livechart.LivechartConfig
import io.github.manamiproject.modb.livechart.LivechartDownloader
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property xmlExtractor
 * @property jsonExtractor
 */
class LivechartRawScoreLoader(
    private val config: MetaDataProviderConfig = LivechartConfig,
    private val downloader: Downloader = LivechartDownloader(config),
    private val xmlExtractor: DataExtractor = XmlDataExtractor,
    private val jsonExtractor: DataExtractor = JsonDataExtractor,
): RawScoreLoader {

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
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