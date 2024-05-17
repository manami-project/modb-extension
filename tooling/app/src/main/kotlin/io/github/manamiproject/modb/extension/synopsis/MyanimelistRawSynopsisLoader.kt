package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.core.extensions.normalize
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.mal.MalConfig
import io.github.manamiproject.modb.mal.MalDownloader
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property extractor
 */
class MyanimelistRawSynopsisLoader(
    private val config: MetaDataProviderConfig = MalConfig,
    private val downloader: Downloader = MalDownloader(config),
    private val extractor: DataExtractor = XmlDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
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