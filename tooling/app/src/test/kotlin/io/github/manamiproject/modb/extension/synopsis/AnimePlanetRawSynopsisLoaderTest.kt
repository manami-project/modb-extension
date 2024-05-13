package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.animeplanet.AnimePlanetConfig
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI
import kotlin.test.Test

internal class AnimePlanetRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anime-planet/synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnimePlanetRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnimePlanetConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("A collaboration between the video game company Hoyoverse and the anime studio ufotable to promote an upcoming animation collaboration project for Genshin Impact.")
        }
    }

    @Test
    fun `successfully load synopsis - includes source`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anime-planet/includes-source.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnimePlanetRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnimePlanetConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land. With the great struggle over, they all go their separate ways to live a quiet life. But as an elf, Frieren, nearly immortal, will long outlive the rest of her former party. How will she come to terms with the mortality of her friends? How can she find fulfillment in her own life, and can she learn to understand what life means to the humans around her? Frieren begins a new journey to find the answer.")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["season-info", "sequel-to", "third-season", "chibi-shorts-for", "continuation-of", "alphanumeric-season"])
    fun `returns NoRawSynopsis - only season info`(input: String) {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anime-planet/$input.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnimePlanetRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnimePlanetConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }

    @Test
    fun `returns NoRawSynopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anime-planet/no-synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnimePlanetRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnimePlanetConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}