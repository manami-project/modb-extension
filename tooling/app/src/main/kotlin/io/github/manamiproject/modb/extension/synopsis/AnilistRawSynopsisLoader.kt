package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.anilist.AnilistConfig
import io.github.manamiproject.modb.anilist.AnilistDefaultTokenRepository
import io.github.manamiproject.modb.anilist.AnilistDefaultTokenRetriever
import io.github.manamiproject.modb.anilist.AnilistDownloader
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.core.extensions.normalize
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.rawdata.DefaultRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import kotlinx.coroutines.runBlocking
import org.apache.commons.text.StringEscapeUtils
import java.net.URI

/**
 * @since 1.0.0
 * @property config
 * @property rawDataRetriever
 * @property extractor
 */
class AnilistRawSynopsisLoader(
    private val appConfig: Config,
    private val config: MetaDataProviderConfig = AnilistConfig,
    private val rawDataRetriever: RawDataRetriever = DefaultRawDataRetriever(
        appConfig = appConfig,
        config = config,
        downloader = AnilistDownloader(config),
    ),
    private val extractor: DataExtractor = JsonDataExtractor,
): RawSynopsisLoader {

    init {
        runBlocking {
            AnilistDefaultTokenRepository.token = AnilistDefaultTokenRetriever().retrieveToken()
        }
    }

    override suspend fun loadRawSynopsis(source: URI): RawSynopsisReturnValue {
        val id = config.extractAnimeId(source)
        val content = rawDataRetriever.retrieveRawData(id)

        val data = extractor.extract(content, mapOf(
            "synopsis" to "$.data.Media.description",
        ))

        val normalizedText = if (data.notFound("synopsis")) {
            EMPTY
        } else {
            normalize(data.stringOrDefault("synopsis"))
        }

        return if (normalizedText.eitherNullOrBlank()) {
            NoRawSynopsis
        } else {
            RawSynopsis(normalizedText)
        }
    }

    private fun normalize(value: String): String {
        return StringEscapeUtils.unescapeHtml4(value.normalize())
            .replace("""<br>\s?(<\/?[a-zA-Z]>)?Notes?:.*?$""".toRegex(), " ")
            .replace("""<\/?[a-zA-Z]>\*.*?$""".toRegex(), " ")
            .splitToSequence("<br>")
            .filterNot { it.eitherNullOrBlank() }
            .filterNot { it.matches("""^\s?Adaptation of .*?$""".toRegex()) }
            .filterNot { it.matches("""^\s?\(Source: .*?$""".toRegex()) }
            .filterNot { it.matches("""^The .*? (cour|season) of .*?$""".toRegex()) }
            .joinToString(" ")
            .replace("""<\/?[a-zA-Z]>""".toRegex(), " ")
            .normalize()
    }
}