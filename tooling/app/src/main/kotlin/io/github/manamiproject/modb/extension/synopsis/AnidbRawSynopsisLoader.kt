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
 * Loads the synopsis from anidb.net
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider. **Default:** [AnidbConfig]
 * @property rawDataRetriever Handles the retrieval of raw data from the meta data provider so that the source doesn't matter for the caller.
 * @property extractor Extracts specific data from the raw data.
 */
class AnidbRawSynopsisLoader(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig = AnidbConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        metaDataProviderConfig = metaDataProviderConfig,
        downloader = AnidbDownloader(metaDataProviderConfig),
    ),
    private val extractor: DataExtractor = XmlDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = metaDataProviderConfig.extractAnimeId(source)
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