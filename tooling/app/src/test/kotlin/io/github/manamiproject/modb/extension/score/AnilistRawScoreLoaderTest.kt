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

internal class AnilistRawScoreLoaderTest {

    @Test
    fun `successfully load score`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("score/anilist/score.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawScoreLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://anilist.co/anime/1535"))

            // then
            assertThat((result as RawScore).scaledValue()).isEqualTo(9.090909090909092)
        }
    }

    @Test
    fun `returns NoRawScore`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("score/anilist/no-score.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawScoreLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://anilist.co/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawScore)
        }
    }
}