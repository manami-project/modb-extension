package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.kitsu.KitsuConfig
import io.github.manamiproject.modb.kitsu.KitsuDownloader
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property extractor
 */
class KitsuRawScoreLoader(
    private val config: MetaDataProviderConfig = KitsuConfig,
    private val downloader: Downloader = KitsuDownloader(config),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawScoreLoader {

    override suspend fun loadRawScore(source: URI): RawScoreReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
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