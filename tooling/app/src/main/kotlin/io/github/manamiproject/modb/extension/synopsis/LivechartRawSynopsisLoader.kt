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
 * @since 1.0.0
 * @property metaDataProviderConfig
 * @property rawDataRetriever
 * @property xmlExtractor
 * @property jsonExtractor
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