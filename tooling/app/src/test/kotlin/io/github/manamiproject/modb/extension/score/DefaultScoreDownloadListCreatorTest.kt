package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.extension.ExtensionData
import io.github.manamiproject.modb.extension.TestConfig
import io.github.manamiproject.modb.extension.config.Config
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

internal class DefaultScoreDownloadListCreatorTest {

    @Nested
    inner class CreateDownloadListTests {

        @Test
        fun `return file without score`() {
            tempDirectory {
                // given
                val testConfig = object: Config by TestConfig {
                    override fun dataDirectory(): Directory = tempDir
                }
                val scoreDownloadListCreator = DefaultScoreDownloadListCreator(testConfig)
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
                val testConfig = object: Config by TestConfig {
                    override fun dataDirectory(): Directory = tempDir
                    override fun clock(): Clock = Clock.fixed(Instant.parse("2021-01-31T16:02:42.00Z"), UTC)
                }
                val scoreDownloadListCreator = DefaultScoreDownloadListCreator(testConfig)
                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    ),
                    score = Score(
                        lastUpdate = LocalDate.now(testConfig.clock()).minusMonths(6L).minusDays(1L).format(ISO_LOCAL_DATE),
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
                val testConfig = object: Config by TestConfig {
                    override fun dataDirectory(): Directory = tempDir
                    override fun clock(): Clock = Clock.systemDefaultZone()
                }
                val scoreDownloadListCreator = DefaultScoreDownloadListCreator(testConfig)
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