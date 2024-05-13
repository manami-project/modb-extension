package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class AnimePlanetRawScoreLoaderTest {

    @Test
    fun `successfully load score`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("score/anime-planet/score.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnimePlanetRawScoreLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://anime-planet.com/anime/1535"))

            // then
            assertThat((result as RawScore).scaledValue()).isEqualTo(8.844)
        }
    }

    @Test
    fun `returns NoRawScore`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("score/anime-planet/no-score.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnimePlanetRawScoreLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://anime-planet.com/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawScore)
        }
    }
}