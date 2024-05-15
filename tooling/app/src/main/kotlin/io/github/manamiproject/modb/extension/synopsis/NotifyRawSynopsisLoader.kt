package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.notify.NotifyConfig
import io.github.manamiproject.modb.notify.NotifyDownloader
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property extractor
 */
class NotifyRawSynopsisLoader(
    private val config: MetaDataProviderConfig = NotifyConfig,
    private val downloader: Downloader = NotifyDownloader(config),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
        val data = extractor.extract(content, mapOf(
            "synopsis" to "$.summary",
        ))

        val normalized = if (data.notFound("synopsis")) {
            EMPTY
        } else {
            normalized(data.stringOrDefault("synopsis"))
        }

        return if (normalized.isBlank()) {
            return NoRawSynopsis
        } else {
            RawSynopsis(normalized)
        }
    }

    private fun normalized(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value)
            .replace(" ", " ")
            .replace("\t", " ")
            .replace("""\(?Source: .*?(\)|$)""".toRegex(), EMPTY)
            .replace("""\[[w|W]ritten by .*?(\]|$)""".toRegex(), EMPTY)
            .replace("\n", " ")
            .replace(""" {2,}""".toRegex(), " ")
            .trim()
    }
}