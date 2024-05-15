package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.kitsu.KitsuConfig
import io.github.manamiproject.modb.kitsu.KitsuDownloader
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property extractor
 */
class KitsuRawSynopsisLoader(
    private val config: MetaDataProviderConfig = KitsuConfig,
    private val downloader: Downloader = KitsuDownloader(config),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
        val data = extractor.extract(content, mapOf(
            "synopsis" to "$.data.attributes.synopsis",
        ))

        val normalized = if (data.notFound("synopsis")) {
            EMPTY
        } else {
            normalize(data.stringOrDefault("synopsis"))
        }

        return if (normalized.isBlank()) {
            NoRawSynopsis
        } else {
            RawSynopsis(normalized)
        }
    }

    private fun normalize(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value)
            .replace(" ", " ")
            .replace("\t", " ")
            .replace("""\(Source: .*?(\)|$)""".toRegex(), " ")
            .replace("""\[Written by .*?(\]|$)""".toRegex(), " ")
            .replace("""^(The )?\w* season of .*?(\.|$)""".toRegex(), " ")
            .replace("\n", " ")
            .replace(""" {2,}""".toRegex(), " ")
            .trim()
    }
}