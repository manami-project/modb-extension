package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import java.net.URI
import kotlin.test.Test

internal class FunctionsKtTest {

    @Nested
    inner class FilenameTests {

        @Test
        fun `throws exception if sources is empty`() {
            // given
            val sources = emptySet<URI>()

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                filename(sources)
            }

            // then
            assertThat(result).hasMessage("Sources must not be empty.")
        }

        @Test
        fun `use sip hash to create filename`() {
            // given
            val sources = setOf(
                URI("https://anidb.net/anime/4563"),
                URI("https://anilist.co/anime/1535"),
                URI("https://anime-planet.com/anime/death-note"),
                URI("https://anisearch.com/anime/3633"),
                URI("https://kitsu.io/anime/1376"),
                URI("https://livechart.me/anime/3437"),
                URI("https://myanimelist.net/anime/1535"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
            )

            // when
            val result = filename(sources)

            // then
            assertThat(result).isEqualTo("049073efcafb1e52.json")
        }

        @Test
        fun `uses distinct list of sources for creating hash`() {
            // given
            val sources = setOf(
                URI("https://anidb.net/anime/4563"),
                URI("https://anidb.net/anime/4563"),
                URI("https://anilist.co/anime/1535"),
                URI("https://anilist.co/anime/1535"),
                URI("https://anilist.co/anime/1535"),
                URI("https://anime-planet.com/anime/death-note"),
                URI("https://anime-planet.com/anime/death-note"),
                URI("https://anisearch.com/anime/3633"),
                URI("https://kitsu.io/anime/1376"),
                URI("https://livechart.me/anime/3437"),
                URI("https://myanimelist.net/anime/1535"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
            )

            val distinctList = sources.distinct()

            // when
            val result = filename(sources)

            // then
            assertThat(result).isEqualTo(filename(distinctList))
        }

        @Test
        fun `uses a sorted list of sources for creating hash`() {
            // given
            val sources = setOf(
                URI("https://notify.moe/anime/0-A-5Fimg"),
                URI("https://anilist.co/anime/1535"),
                URI("https://myanimelist.net/anime/1535"),
                URI("https://anisearch.com/anime/3633"),
                URI("https://livechart.me/anime/3437"),
                URI("https://kitsu.io/anime/1376"),
                URI("https://anime-planet.com/anime/death-note"),
                URI("https://anidb.net/anime/4563"),
            )

            val sorted = sources.sorted()

            // when
            val result = filename(sources)

            // then
            assertThat(result).isEqualTo(filename(sorted))
        }

        @Test
        fun `correctly adjust suffix`() {
            // given
            val sources = setOf(
                URI("https://anidb.net/anime/4563"),
                URI("https://anilist.co/anime/1535"),
                URI("https://anime-planet.com/anime/death-note"),
                URI("https://anisearch.com/anime/3633"),
                URI("https://kitsu.io/anime/1376"),
                URI("https://livechart.me/anime/3437"),
                URI("https://myanimelist.net/anime/1535"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
            )

            // when
            val result = filename(sources, EMPTY)

            // then
            assertThat(result).isEqualTo("049073efcafb1e52")
        }
    }
}