package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.animeplanet.AnimePlanetConfig
import io.github.manamiproject.modb.animeplanet.AnimePlanetDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property xmlExtractor
 * @property jsonExtractor
 */
class AnimePlanetRawSynopsisLoader(
    private val config: MetaDataProviderConfig = AnimePlanetConfig,
    private val downloader: Downloader = AnimePlanetDownloader(config),
    private val xmlExtractor: DataExtractor = XmlDataExtractor,
    private val jsonExtractor: DataExtractor = JsonDataExtractor,
): RawSynopsisLoader {

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
        val data = xmlExtractor.extract(content, mapOf(
            "jsonld" to "//script[@type='application/ld+json']/node()",
        ))

        val jsonld = data.string("jsonld")
        val jsonldData = jsonExtractor.extract(jsonld, mapOf(
            "synopsis" to "$.description",
        ))

        val normalizedText = if (jsonldData.notFound("synopsis")) {
            EMPTY
        } else {
            normalize(jsonldData.stringOrDefault("synopsis"))
        }

        return if (normalizedText.isBlank()) {
             NoRawSynopsis
        } else {
            RawSynopsis(normalizedText)
        }
    }

    private fun normalize(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value)
            .replace(" ", " ")
            .replace("""^(The )?\w* season of .*?(\.|$)""".toRegex(), " ")
            .replace("""^Sequel to .*?(\.|$)""".toRegex(), " ")
            .replace("""^Continuation of .*?(\.|$)""".toRegex(), " ")
            .replace("""^A special recap of .*?(\.|$)""".toRegex(), " ")
            .replace("""^Chibi shorts for .*?(\.|$)""".toRegex(), " ")
            .replace(" {2,}", " ")
            .trim()
    }
}