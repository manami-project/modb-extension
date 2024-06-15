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

internal class AnilistRawScoreLoaderTest {

    @Test
    fun `successfully load score`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("score/anilist/score.json")
            }

            val scoreLoader = AnilistRawScoreLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
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
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("score/anilist/no-score.json")
            }

            val scoreLoader = AnilistRawScoreLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawScore(URI("https://anilist.co/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawScore)
        }
    }
}