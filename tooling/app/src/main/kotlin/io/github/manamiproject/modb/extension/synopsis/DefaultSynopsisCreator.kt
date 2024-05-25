package io.github.manamiproject.modb.extension.synopsis

import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.invokeModel
import io.github.manamiproject.modb.anidb.AnidbConfig
import io.github.manamiproject.modb.anilist.AnilistConfig
import io.github.manamiproject.modb.animeplanet.AnimePlanetConfig
import io.github.manamiproject.modb.anisearch.AnisearchConfig
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.eitherNullOrBlank
import io.github.manamiproject.modb.core.extensions.normalize
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.extension.filename
import io.github.manamiproject.modb.kitsu.KitsuConfig
import io.github.manamiproject.modb.livechart.LivechartConfig
import io.github.manamiproject.modb.myanimelist.MyanimelistConfig
import io.github.manamiproject.modb.notify.NotifyConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.apache.commons.text.similarity.LevenshteinDistance
import java.net.URI

/**
 * @since 1.0.0
 * @property rawSynopsisLoader
 * @property bedrockClient
 * @property extractor
 */
class DefaultSynopsisCreator(
    private val rawSynopsisLoader: Map<Hostname, RawSynopsisLoader> = mapOf(
        AnidbConfig.hostname() to AnidbRawSynopsisLoader(),
        AnilistConfig.hostname() to AnilistRawSynopsisLoader(),
        AnimePlanetConfig.hostname() to AnimePlanetRawSynopsisLoader(),
        AnisearchConfig.hostname() to AnisearchRawSynopsisLoader(),
        KitsuConfig.hostname() to KitsuRawSynopsisLoader(),
        LivechartConfig.hostname() to LivechartRawSynopsisLoader(),
        MyanimelistConfig.hostname() to MyanimelistRawSynopsisLoader(),
        NotifyConfig.hostname() to NotifyRawSynopsisLoader(),
    ),
    private val bedrockClient: BedrockRuntimeClient = BedrockRuntimeClient {
        region = "us-east-1"
    },
    private val extractor: DataExtractor = JsonDataExtractor,
): SynopsisCreator {

    override suspend fun createSynopsis(sources: Collection<URI>): SynopsisReturnValue = withContext(LIMITED_NETWORK) {
        if (sources.size < 3) {
            return@withContext Synopsis(
                text = EMPTY,
                author = MODEL_ID,
            )
        }

        val jobs = mutableListOf<Deferred<RawSynopsisReturnValue>>()

        sources.forEach {
            jobs.add(async { rawSynopsisLoader[it.host]!!.loadRawSynopsis(it) })
        }

        val rawSynopsisList = jobs.awaitAll()
            .asSequence()
            .filterNot { it is NoRawSynopsis }
            .map { it as RawSynopsis }
            .filter { it.wordCount() >= 20 }
            .map { it.text }
            .distinct()
            .toList()

        val nonIdentical = rawSynopsisList.toMutableSet()
        rawSynopsisList.forEach { outer ->
            rawSynopsisList.forEach { inner ->
                val l = LevenshteinDistance.getDefaultInstance().apply(outer, inner)
                if (l in 1..25) {
                    nonIdentical.remove(outer.takeIf { outer.length > inner.length } ?: inner)
                }
            }
        }

        if (nonIdentical.size < 3) {
            return@withContext Synopsis(
                text = EMPTY,
                author = MODEL_ID,
            )
        }

        var prompt: String
        var responseText: String

        do {
            prompt = generatePrompt(nonIdentical)
            val response = bedrockClient.invokeModel {
                modelId = MODEL_ID
                accept = "application/json"
                contentType = "application/json"
                body = """
                {
                    "prompt": "$prompt",
                    "temperature": 0.8,
                    "top_p": 0.9,
                    "top_k": 500,
                    "max_tokens_to_sample": 400
                }
                """.trimIndent().toByteArray()
            }.body.decodeToString()

            val data = extractor.extract(response, mapOf(
                "responseText" to "$.completion"
            ))

            responseText = data.stringOrDefault("responseText")
                .removePrefix("\n")
                .removePrefix("<synopsis>")
                .removeSuffix("</synopsis>")
                .trim()
        } while (responseText.contains("<synopsis>") || responseText.contains("</synopsis>") || responseText.startsWith('.'))

        if (responseText.eitherNullOrBlank()) {
            return@withContext Synopsis(
                text = EMPTY,
                author = MODEL_ID,
            )
        }

        return@withContext Synopsis(
            text = responseText.normalize(),
            author = MODEL_ID,
        )
    }

    private fun generatePrompt(rawSynopsis: Collection<String>): String {
        val promptBuilder = StringBuilder("""
        \n\nHuman: 
        I provide you multiple synopsis, each enclosed within <synopsis> and </synopsis> tags. These synopsis all pertain to the same anime. 

        Your task is to understand the content of the anime based on the information given in the provided synopsis and then generate a new synopsis. While creating the new synopsis, adhere to the following guidelines:

        1. **Fact-based:** The new synopsis should only include information that is explicitly mentioned in the original synopsis. 

        2. **Originality:** Don't copy sentences or parts of sentences from the original synopsis. Instead, aim to rewrite the information in a fresh and engaging manner. Strive for creative and varied openings and sentence structures.

        3. **Purpose:** The primary goal of the new synopsis is to inform readers about the content of the anime. Additionally, the synopsis should pique the reader's interest and potentially encourage them to watch the anime.
        
        By adhering to these guidelines, you can create a synopsis that is informative, engaging, and unique.
        """.trimIndent())

        rawSynopsis.forEach {
            promptBuilder.append("""
            
            <synopsis>${it}</synopsis>
        """.trimIndent())
        }

        promptBuilder.append("\n\nAssistant: <synopsis>")

        return promptBuilder.toString().replace('"', '\'').replace("\n", "\\n")
    }

    private companion object {
        private const val MODEL_ID = "anthropic.claude-v2:1"
    }
}

