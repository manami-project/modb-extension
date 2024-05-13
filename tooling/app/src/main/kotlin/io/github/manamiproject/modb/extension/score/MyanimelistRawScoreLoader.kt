package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.mal.MalConfig
import io.github.manamiproject.modb.mal.MalDownloader
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property extractor
 */
class MyanimelistRawScoreLoader(
    private val config: MetaDataProviderConfig = MalConfig,
    private val downloader: Downloader = MalDownloader(config),
    private val extractor: DataExtractor = XmlDataExtractor,
): RawScoreLoader {

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
        val data = extractor.extract(content, mapOf(
            "score" to "//span[@itemprop='ratingValue']/span/text()",
        ))

        if (data.notFound("score")) {
            return NoRawScore
        }

        val rawScore = data.double("score")

        return if (rawScore == 0.0) {
            NoRawScore
        } else {
            RawScore(rawScore, 1.0..10.0)
        }
    }
}