package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.extension.ExtensionData
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import kotlin.io.path.createFile

internal class DefaultScoreDownloadListCreatorTest {

    @Nested
    inner class ConstructorTests {

        @Test
        fun `throws exception if directory doesn't exist`() {
            tempDirectory {
                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    DefaultScoreDownloadListCreator(tempDir.resolve("unknown"))
                }

                // then
                assertThat(result).hasMessage("Data directory either doesn't exist or is not a directory.")
            }
        }

        @Test
        fun `throws exception if path is not a directory`() {
            tempDirectory {
                // given
                val path = tempDir.resolve("text.txt").createFile()

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    DefaultScoreDownloadListCreator(path)
                }

                // then
                assertThat(result).hasMessage("Data directory either doesn't exist or is not a directory.")
            }
        }
    }

    @Nested
    inner class CreateDownloadListTests {

        @Test
        fun `return file without score`() {
            tempDirectory {
                // given
                val scoreDownloadListCreator = DefaultScoreDownloadListCreator(tempDir)
                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    ),
                )
                Json.toJson(extensionData).writeToFile(tempDir.resolve("6f0e12caa76e9514.json"))

                // when
                val result = scoreDownloadListCreator.createDownloadList()

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    hashSetOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    )
                )
            }
        }

        @Test
        fun `returns file if entry is older than 6 months`() {
            tempDirectory {
                // given
                val clock = Clock.fixed(Instant.parse("2021-01-31T16:02:42.00Z"), UTC)
                val scoreDownloadListCreator = DefaultScoreDownloadListCreator(tempDir, clock)
                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    ),
                    score = Score(
                        lastUpdate = LocalDate.now(clock).minusMonths(6L).minusDays(1L).format(ISO_LOCAL_DATE),
                    ),
                )
                Json.toJson(extensionData).writeToFile(tempDir.resolve("6f0e12caa76e9514.json"))

                // when
                val result = scoreDownloadListCreator.createDownloadList()

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    hashSetOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    )
                )
            }
        }

        @Test
        fun `don't return recent and valid entries`() {
            tempDirectory {
                // given
                val scoreDownloadListCreator = DefaultScoreDownloadListCreator(tempDir)
                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    ),
                    score = Score(
                        arithmeticMean = 5.0,
                        arithmeticGeometricMean = 5.0,
                        median = 5.0,
                    ),
                )
                Json.toJson(extensionData).writeToFile(tempDir.resolve("6f0e12caa76e9514.json"))

                // when
                val result = scoreDownloadListCreator.createDownloadList()

                // then
                assertThat(result).isEmpty()
            }
        }
    }
}