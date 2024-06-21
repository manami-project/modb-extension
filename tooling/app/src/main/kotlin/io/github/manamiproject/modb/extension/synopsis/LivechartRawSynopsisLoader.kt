package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.normalize
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.livechart.LivechartConfig
import io.github.manamiproject.modb.livechart.LivechartDownloader
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * Loads the synopsis from livechart.me
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider. **Default:** [LivechartConfig]
 * @property rawDataRetriever Handles the retrieval of raw data from the meta data provider so that the source doesn't matter for the caller.
 * @property xmlExtractor  Uses XPath to extract data from HTML.
 * @property jsonExtractor Uses JsonPath to extract data from JSON.
 */
class LivechartRawSynopsisLoader(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig = LivechartConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        metaDataProviderConfig = metaDataProviderConfig,
        downloader = LivechartDownloader(metaDataProviderConfig),
    ),
    private val xmlExtractor: DataExtractor = XmlDataExtractor,
    private val jsonExtractor: DataExtractor = JsonDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = metaDataProviderConfig.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = xmlExtractor.extract(content, mapOf(
            "jsonld" to "//script[@type='application/ld+json']/node()",
        ))

        val jsonld = if (data.notFound("jsonld")) {
            EMPTY
        } else {
            data.listNotNull<String>("jsonld").first()
        }
        val jsonldData = jsonExtractor.extract(jsonld, mapOf(
            "synopsis" to "$.description",
        ))

        return if (jsonldData.notFound("synopsis")) {
            NoRawSynopsis
        } else {
            RawSynopsis(normalize(jsonldData.stringOrDefault("synopsis")))
        }
    }

    private fun normalize(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value).normalize()
    }
}