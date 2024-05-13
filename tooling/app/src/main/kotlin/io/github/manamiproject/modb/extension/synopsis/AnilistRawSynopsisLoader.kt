package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.anilist.AnilistConfig
import io.github.manamiproject.modb.anilist.AnilistDefaultTokenRepository
import io.github.manamiproject.modb.anilist.AnilistDefaultTokenRetriever
import io.github.manamiproject.modb.anilist.AnilistDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import kotlinx.coroutines.runBlocking
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property downloader
 * @property extractor
 */
class AnilistRawSynopsisLoader(
    private val config: MetaDataProviderConfig = AnilistConfig,
    private val downloader: Downloader = AnilistDownloader(config),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawSynopsisLoader {

    init {
        runBlocking {
            AnilistDefaultTokenRepository.token = AnilistDefaultTokenRetriever().retrieveToken()
        }
    }

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = downloader.download(id)
        val data = extractor.extract(content, mapOf(
            "synopsis" to "$.data.Media.description",
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
        return StringEscapeUtils.unescapeHtml4(value.replace("\n", " ")
            .replace("""<br>\s?(<\/?[a-zA-Z]>)?Notes?:.*?$""".toRegex(), " ")
            .replace("""<\/?[a-zA-Z]>\*.*?$""".toRegex(), " ")
            .replace(" ", " ")
            .splitToSequence("<br>")
            .filterNot { it.isBlank() }
            .filterNot { it.matches("""^\s?Adaptation of .*?$""".toRegex()) }
            .filterNot { it.matches("""^\s?\(Source: .*?$""".toRegex()) }
            .filterNot { it.matches("""^The .*? (cour|season) of .*?$""".toRegex()) }
            .joinToString(" ")
            .replace("""<\/?[a-zA-Z]>""".toRegex(), " ")
            .replace(""" {2,}""".toRegex(), " "))
            .trim()
    }
}