package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.core.extensions.normalize
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.myanimelist.MyanimelistConfig
import io.github.manamiproject.modb.myanimelist.MyanimelistDownloader
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * Loads the synopsis from myanimelist.net
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider. **Default:** [MyanimelistConfig]
 * @property rawDataRetriever Handles the retrieval of raw data from the meta data provider so that the source doesn't matter for the caller.
 * @property extractor Extracts specific data from the raw data.
 */
class MyanimelistRawSynopsisLoader(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig = MyanimelistConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        metaDataProviderConfig = metaDataProviderConfig,
        downloader = MyanimelistDownloader(metaDataProviderConfig),
    ),
    private val extractor: DataExtractor = XmlDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = metaDataProviderConfig.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = extractor.extract(content, mapOf(
            "synopsis" to "//p[@itemprop='description']//text()",
        ))

        val normalized = when {
            data.notFound("synopsis") -> EMPTY
            data.stringOrDefault("synopsis").trim().startsWith("No synopsis information has been added to this title.") -> EMPTY
            else -> normalize(data.stringOrDefault("synopsis"))
        }

        return if (normalized.eitherNullOrBlank()) {
            NoRawSynopsis
        } else {
            RawSynopsis(normalized)
        }
    }

    private fun normalize(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value)
            .replace("""\[[w|W]ritten by .*?(\]|$)""".toRegex(), EMPTY)
            .replace("""\(Source: .*?\)""".toRegex(), EMPTY)
            .replace("""^(The )?\w* season of .*?(\.|$)""".toRegex(), " ")
            .normalize()
    }
}