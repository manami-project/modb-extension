package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.models.Anime
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test
import java.net.URI

internal class ScoreReaderKtTest {

    @Test
    fun `default implementation with anime as argument is just a delegate`() {
        runBlocking {
            // given
            val result = mutableListOf<URI>()
            val testImpl = object : ScoreReader {
                override suspend fun findScore(sources: Collection<URI>): ScoreReturnValue {
                    result.addAll(sources)
                    return ScoreNotFound
                }
            }

            val testAnime = Anime(
                sources = hashSetOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
                _title = "Test",
            )

            // when
            testImpl.findScore(testAnime)

            // then
            assertThat(result).containsExactlyInAnyOrder(*testAnime.sources.toTypedArray())
        }
    }
}