package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.anidb.AnidbConfig
import io.github.manamiproject.modb.anidb.AnidbDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.core.extensions.normalize
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property rawDataRetriever
 * @property extractor
 */
class AnidbRawSynopsisLoader(
    private val appConfig: Config,
    private val config: MetaDataProviderConfig = AnidbConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        config = config,
        downloader = AnidbDownloader(config),
    ),
    private val extractor: DataExtractor = XmlDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = extractor.extract(content, mapOf(
            "synopsis" to "//div[@itemprop='description']/text()",
        ))

        val normalizedText = if (data.notFound("synopsis")) {
            EMPTY
        } else {
            normalize(data.stringOrDefault("synopsis"))
        }

        return if (normalizedText.eitherNullOrBlank()) {
            NoRawSynopsis
        } else {
            RawSynopsis(normalizedText)
        }
    }

    private fun normalize(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value
            .replace("""\* .*?(\.|$)""".toRegex(), EMPTY)
            .replace("""~ Description by .*?$""".toRegex(), EMPTY)
            .replace("""Source: .*?$""".toRegex(), EMPTY)
            .replace("""Note( ?\d?): .*?$""".toRegex(), EMPTY)
            .replace("""~ translated .*?$""".toRegex(), EMPTY)
            .replace("""— written by .*?$""".toRegex(), EMPTY)
        ).normalize()
    }
}