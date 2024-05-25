package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.extension.*
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

internal class DefaultSynopsisWriterTest {

    @Test
    fun `correctly create new entry using default values from Synopsis for created and lastUpdate`() {
        tempDirectory {
            // given
            val testSources = listOf(
                URI("https://anidb.net/anime/4563"),
                URI("https://anilist.co/anime/1535"),
                URI("https://anime-planet.com/anime/death-note"),
                URI("https://anisearch.com/anime/3633"),
                URI("https://kitsu.io/anime/1376"),
                URI("https://livechart.me/anime/3437"),
                URI("https://myanimelist.net/anime/1535"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
            )

            val testSynopsis = Synopsis(
                text = "text-value",
                author = "author-value",
            )

            var receivedObject: ExtensionData? = null

            val clock = Clock.fixed(Instant.parse("2021-01-31T16:02:42.00Z"), UTC)
            val today = LocalDate.now().format(ISO_LOCAL_DATE)
            val origin = LocalFileOrigin(tempDir)
            val testFileAccessor = object : FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue {
                    return ExtensionDataNotFound
                }
                override suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData) {
                    receivedObject = extensionData
                }
            }
            val writer = DefaultSynopsisWriter(
                directory = origin,
                fileAccessor = testFileAccessor,
                clock = clock,
            )

            // when
            writer.saveOrUpdateSynopsis(testSources, testSynopsis)

            // then
            assertThat(receivedObject).isEqualTo(ExtensionData(
                sources = testSources,
                synopsis = testSynopsis,
            ))
            assertThat((receivedObject!!.synopsis() as Synopsis).lastUpdatedAt).isEqualTo(today)
        }
    }

    @Test
    fun `correctly update existing entry using`() {
        tempDirectory {
            // given
            val testSources = listOf(
                URI("https://anidb.net/anime/4563"),
                URI("https://anilist.co/anime/1535"),
                URI("https://anime-planet.com/anime/death-note"),
                URI("https://anisearch.com/anime/3633"),
                URI("https://kitsu.io/anime/1376"),
                URI("https://livechart.me/anime/3437"),
                URI("https://myanimelist.net/anime/1535"),
                URI("https://notify.moe/anime/0-A-5Fimg"),
            )

            val testSynopsis = Synopsis(
                text = "text-value",
                author = "author-value",
                lastUpdate = "2024-03-06",
            )

            var receivedObject: ExtensionData? = null

            val clock = Clock.fixed(Instant.parse("2024-05-16T16:02:42.00Z"), UTC)
            val origin = LocalFileOrigin(tempDir)
            val testFileAccessor = object : FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue {
                    return ExtensionData(
                        sources = testSources,
                        synopsis = testSynopsis.copy(
                            text = "previous value",
                        ),
                    )
                }
                override suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData) {
                    receivedObject = extensionData
                }
            }
            val writer = DefaultSynopsisWriter(
                directory = origin,
                fileAccessor = testFileAccessor,
                clock = clock,
            )

            // when
            writer.saveOrUpdateSynopsis(testSources, testSynopsis)

            // then
            assertThat(receivedObject!!.sources).isEqualTo(testSources)
            assertThat((receivedObject!!.synopsis() as Synopsis).text).isEqualTo(testSynopsis.text)
            assertThat((receivedObject!!.synopsis() as Synopsis).author).isEqualTo(testSynopsis.author)
            assertThat((receivedObject!!.synopsis() as Synopsis).lastUpdatedAt).isEqualTo(LocalDate.now(clock))
        }
    }

    @Test
    fun `throws exception if sources is empty`() {
        tempDirectory {
            // given
            val writer = DefaultSynopsisWriter(
                directory = LocalFileOrigin(tempDir),
                fileAccessor = TestFileAccessor,
            )
            val testSynopsis = Synopsis(
                text = "text",
                author = "me",
            )

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                writer.saveOrUpdateSynopsis(emptySet(), testSynopsis)
            }

            // then
            assertThat(result).hasMessage("Sources must not be empty.")
        }
    }
}