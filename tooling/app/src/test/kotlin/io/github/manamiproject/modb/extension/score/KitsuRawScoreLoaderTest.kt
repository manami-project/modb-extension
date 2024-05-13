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

internal class KitsuRawScoreLoaderTest {

    @Test
    fun `successfully load score`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("score/kitsu/score.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = KitsuRawScoreLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://kitsu.io/anime/1535"))

            // then
            assertThat((result as RawScore).scaledValue()).isEqualTo(7.893636363636364)
        }
    }

    @Test
    fun `returns NoRawScore`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("score/kitsu/no-score.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = KitsuRawScoreLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://kitsu.io/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawScore)
        }
    }
}