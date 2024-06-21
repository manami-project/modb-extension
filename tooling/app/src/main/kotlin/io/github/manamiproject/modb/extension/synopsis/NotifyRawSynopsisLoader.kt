package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.core.extensions.normalize
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.notify.NotifyConfig
import io.github.manamiproject.modb.notify.NotifyDownloader
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * Loads the synopsis from notify.moe
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider. **Default:** [NotifyConfig]
 * @property rawDataRetriever Handles the retrieval of raw data from the meta data provider so that the source doesn't matter for the caller.
 * @property extractor Extracts specific data from the raw data.
 */
class NotifyRawSynopsisLoader(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig = NotifyConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        metaDataProviderConfig = metaDataProviderConfig,
        downloader = NotifyDownloader(metaDataProviderConfig),
    ),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = metaDataProviderConfig.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = extractor.extract(content, mapOf(
            "synopsis" to "$.summary",
        ))

        val normalized = if (data.notFound("synopsis")) {
            EMPTY
        } else {
            normalized(data.stringOrDefault("synopsis"))
        }

        return if (normalized.eitherNullOrBlank()) {
            return NoRawSynopsis
        } else {
            RawSynopsis(normalized)
        }
    }

    private fun normalized(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value)
            .replace("""\(?Source: .*?(\)|$)""".toRegex(), EMPTY)
            .replace("""\[[w|W]ritten by .*?(\]|$)""".toRegex(), EMPTY)
            .normalize()
    }
}