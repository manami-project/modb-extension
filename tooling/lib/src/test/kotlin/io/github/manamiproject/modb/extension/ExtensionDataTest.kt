package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.extension.score.Score
import io.github.manamiproject.modb.extension.score.ScoreNoteFound
import io.github.manamiproject.modb.extension.synopsis.Synopsis
import io.github.manamiproject.modb.extension.synopsis.SynopsisNotFound
import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI
import kotlin.test.Test

internal class ExtensionDataTest {

    @Nested
    inner class ConstructorTests {

        @Test
        fun `throws exception if sources is empty`() {
            // given

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                ExtensionData(
                    sources = emptyList(),
                )
            }

            // then
            assertThat(result).hasMessage("Sources must not be empty")
        }
    }

    @Nested
    inner class SynopsisTests {

        @Test
        fun `returns SynopsisNotFound if synopsis in unavailable`() {
            // given
            val extensionData = ExtensionData(
                sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
            )

            // when
            val result = extensionData.synopsis()

            // then
            assertThat(result).isEqualTo(SynopsisNotFound)
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "  "])
        fun `returns SynopsisNotFound if synopsis text is blank`(input: String) {
            // given
            val extensionData = ExtensionData(
                sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
                synopsis = Synopsis(
                    text = input,
                    author = "me",
                    hash = "049073efcafb1e52",
                )
            )

            // when
            val result = extensionData.synopsis()

            // then
            assertThat(result).isEqualTo(SynopsisNotFound)
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "  "])
        fun `returns SynopsisNotFound if synopsis author is blank`(input: String) {
            // given
            val extensionData = ExtensionData(
                sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
                synopsis = Synopsis(
                    text = "text",
                    author = input,
                    hash = "049073efcafb1e52",
                )
            )

            // when
            val result = extensionData.synopsis()

            // then
            assertThat(result).isEqualTo(SynopsisNotFound)
        }

        @Test
        fun `correctly returns Synopsis`() {
            // given
            val synopsis = Synopsis(
                text = "test",
                author = "me",
                hash = "049073efcafb1e52",
            )
            val extensionData = ExtensionData(
                sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
                synopsis = synopsis,
            )

            // when
            val result = extensionData.synopsis()

            // then
            assertThat(result).isEqualTo(synopsis)
        }
    }

    @Nested
    inner class ScoreTests {

        @Test
        fun `returns ScoreNotFound if score in unavailable`() {
            // given
            val extensionData = ExtensionData(
                sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
            )

            // when
            val result = extensionData.score()

            // then
            assertThat(result).isEqualTo(ScoreNoteFound)
        }

        @Test
        fun `returns ScoreNotFound if score has been checked, but all values are zero`() {
            // given
            val extensionData = ExtensionData(
                sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
                score = Score(
                    hash = "049073efcafb1e52",
                ),
            )

            // when
            val result = extensionData.score()

            // then
            assertThat(result).isEqualTo(ScoreNoteFound)
        }

        @Test
        fun `correctly returns Score`() {
            // given
            val score = Score(
                arithmeticMean = 5.0,
                hash = "049073efcafb1e52",
            )
            val extensionData = ExtensionData(
                sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                ),
                score = score,
            )

            // when
            val result = extensionData.score()

            // then
            assertThat(result).isEqualTo(score)
        }
    }
}