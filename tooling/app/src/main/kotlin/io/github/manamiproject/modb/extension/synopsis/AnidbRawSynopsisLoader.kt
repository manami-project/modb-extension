package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.anidb.AnidbConfig
import io.github.manamiproject.modb.anidb.AnidbDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property extractor
 */
class AnidbRawSynopsisLoader(
    private val config: MetaDataProviderConfig = AnidbConfig,
    private val downloader: Downloader = AnidbDownloader(config),
    private val extractor: DataExtractor = XmlDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
        val data = extractor.extract(content, mapOf(
            "synopsis" to "//div[@itemprop='description']/text()",
        ))

        val normalizedText = if (data.notFound("synopsis")) {
            EMPTY
        } else {
            normalize(data.stringOrDefault("synopsis"))
        }

        return if (normalizedText.isBlank()) {
            NoRawSynopsis
        } else {
            RawSynopsis(normalizedText)
        }
    }

    private fun normalize(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value.replace("\n", EMPTY)
            .replace(" ", " ")
            .replace("""\* .*?(\.|$)""".toRegex(), EMPTY)
            .replace("""~ Description by .*?$""".toRegex(), EMPTY)
            .replace("""Source: .*?$""".toRegex(), EMPTY)
            .replace("""Note( ?\d?): .*?$""".toRegex(), EMPTY)
            .replace("""~ translated .*?$""".toRegex(), EMPTY)
            .replace("""— written by .*?$""".toRegex(), EMPTY))
            .trim()
    }
}