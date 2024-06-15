package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.extension.TestConfig
import io.github.manamiproject.modb.extension.TestRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.kitsu.KitsuConfig
import io.github.manamiproject.modb.test.loadTestResource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class KitsuRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/kitsu/synopsis.json")
            }

            val scoreLoader = KitsuRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${KitsuConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Monkey tricks crab and steals his food.")
        }
    }

    @Test
    fun `successfully load synopsis - written by`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/kitsu/written-by.json")
            }

            val scoreLoader = KitsuRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${KitsuConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Having been accepted into the Kaede Inn, Nana struggles to find some way to contribute, though she inadvertently brings more trouble than assistance. However, Nana's worries are directed more towards fellow resident Nyu, whom she had only known as Lucy, the violent Diclonius. Fearful that Nyu will unleash the same horrific savagery—violence that scars Nana to this day—upon those close to her, Nana faces a dilemma: attempt to live peacefully alongside Lucy with all the uncertainty that that entails or dispose of the source of her worries, shattering the relationships she has formed at the inn. As Nana struggles to come to a decision, Nyu recalls a painful memory of one of her dearest friends and one of her greatest rivals.")
        }
    }

    @Test
    fun `returns NoRawSynopsis - only-season-info`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/kitsu/only-season-info.json")
            }

            val scoreLoader = KitsuRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${KitsuConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }

    @Test
    fun `returns NoRawSynopsis`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/kitsu/no-synopsis.json")
            }

            val scoreLoader = KitsuRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${KitsuConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}