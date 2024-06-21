package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.extension.*
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC
import kotlin.io.path.createFile
import kotlin.test.Test

internal class DefaultScoreWriterTest {

    @Test
    fun `throws exception if sources is empty`() {
        tempDirectory {
            // given
            val writer = DefaultScoreWriter(
                directory = LocalFileOrigin(tempDir),
                fileAccessor = TestFileAccessor,
            )

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                writer.saveOrUpdateScore(emptySet(), Score())
            }

            // then
            assertThat(result).hasMessage("Sources must not be empty.")
        }
    }

    @Test
    fun `correctly create new entry`() {
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

            val testScore = Score(
                arithmeticMean = 1.0,
                arithmeticGeometricMean = 2.0,
                median = 3.0,
            )

            var receivedObject: ExtensionData? = null

            val clock = Clock.fixed(Instant.parse("2021-01-31T16:02:42.00Z"), UTC)
            val origin = LocalFileOrigin(tempDir)
            val testFileAccessor = object : FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue {
                    return ExtensionDataNotFound
                }
                override suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData) {
                    receivedObject = extensionData
                }
            }
            val writer = DefaultScoreWriter(
                directory = origin,
                fileAccessor = testFileAccessor,
                clock = clock,
            )

            // when
            writer.saveOrUpdateScore(testSources, testScore)

            // then
            assertThat(receivedObject).isEqualTo(
                ExtensionData(
                    sources = testSources,
                    score = testScore,
                )
            )
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

            val testScore = Score(
                arithmeticMean = 1.0,
                arithmeticGeometricMean = 2.0,
                median = 3.0,
            )

            var receivedObject: ExtensionData? = null

            val clock = Clock.fixed(Instant.parse("2024-05-16T16:02:42.00Z"), UTC)
            val origin = LocalFileOrigin(tempDir)
            val testFileAccessor = object : FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue {
                    return ExtensionData(
                        sources = testSources,
                        score = testScore.copy(
                            arithmeticMean = 4.0,
                            arithmeticGeometricMean = 5.0,
                            median = 6.0,
                        ),
                    )
                }
                override suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData) {
                    receivedObject = extensionData
                }
            }
            val writer = DefaultScoreWriter(
                directory = origin,
                fileAccessor = testFileAccessor,
                clock = clock,
            )

            // when
            writer.saveOrUpdateScore(testSources, testScore)

            // then
            assertThat(receivedObject!!.sources).isEqualTo(testSources)
            assertThat((receivedObject!!.score() as Score).arithmeticMean).isEqualTo(testScore.arithmeticMean)
            assertThat((receivedObject!!.score() as Score).arithmeticGeometricMean).isEqualTo(testScore.arithmeticGeometricMean)
            assertThat((receivedObject!!.score() as Score).median).isEqualTo(testScore.median)
        }
    }

    @Test
    fun `removes old file if sources changed`() {
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

            val testScore = Score(
                arithmeticMean = 1.0,
                arithmeticGeometricMean = 2.0,
                median = 3.0,
            )

            tempDir.resolve("abcdtest.json").createFile()
            var receivedObject: ExtensionData? = null

            val clock = Clock.fixed(Instant.parse("2024-05-16T16:02:42.00Z"), UTC)
            val origin = LocalFileOrigin(tempDir)
            val testFileAccessor = object : FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue {
                    return ExtensionData(
                        sources = testSources,
                        score = testScore.copy(
                            arithmeticMean = 4.0,
                            arithmeticGeometricMean = 5.0,
                            median = 6.0,
                        ),
                    )
                }
                override suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData) {
                    receivedObject = extensionData
                }
            }
            val writer = DefaultScoreWriter(
                directory = origin,
                fileAccessor = testFileAccessor,
                clock = clock,
            )

            // when
            writer.saveOrUpdateScore(testSources, testScore)

            // then
            assertThat(receivedObject!!.sources).isEqualTo(testSources)
            assertThat((receivedObject!!.score() as Score).arithmeticMean).isEqualTo(testScore.arithmeticMean)
            assertThat((receivedObject!!.score() as Score).arithmeticGeometricMean).isEqualTo(testScore.arithmeticGeometricMean)
            assertThat((receivedObject!!.score() as Score).median).isEqualTo(testScore.median)
            assertThat(tempDir.resolve("abcdtest")).doesNotExist()
        }
    }
}