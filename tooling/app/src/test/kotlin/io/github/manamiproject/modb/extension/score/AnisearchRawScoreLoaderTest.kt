package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.extension.TestConfig
import io.github.manamiproject.modb.extension.TestRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.test.loadTestResource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class AnisearchRawScoreLoaderTest {

    @Test
    fun `successfully load score`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("score/anisearch/score.html")
            }

            val scoreLoader = AnisearchRawScoreLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://anisearch.com/anime/1535"))

            // then
            assertThat((result as RawScore).scaledValue()).isEqualTo(9.06326530612245)
        }
    }

    @Test
    fun `returns NoRawScore`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("score/anisearch/no-score.html")
            }

            val scoreLoader = AnisearchRawScoreLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://anisearch.com/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawScore)
        }
    }
}