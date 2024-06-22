package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.extension.ExtensionData
import io.github.manamiproject.modb.extension.TestConfig
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

internal class DefaultSynopsisDownloadListCreatorTest {

    @Nested
    inner class CreateDownloadListTests {

        @Test
        fun `return file without score`() {
            tempDirectory {
                // given
                val testConfig = object: Config by TestConfig {
                    override fun dataDirectory(): Directory = tempDir
                }
                val synopsisDownloadListCreator = DefaultSynopsisDownloadListCreator(testConfig)
                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    ),
                )
                Json.toJson(extensionData).writeToFile(tempDir.resolve("6f0e12caa76e9514.json"))

                // when
                val result = synopsisDownloadListCreator.createDownloadList()

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
                val synopsisDownloadListCreator = DefaultSynopsisDownloadListCreator(testConfig)
                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    ),
                    synopsis = Synopsis(
                        text = "text",
                        author = "me",
                        lastUpdate = LocalDate.now(testConfig.clock()).minusMonths(6L).minusDays(1L).format(ISO_LOCAL_DATE),
                    ),
                )
                Json.toJson(extensionData).writeToFile(tempDir.resolve("6f0e12caa76e9514.json"))

                // when
                val result = synopsisDownloadListCreator.createDownloadList()

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
                val synopsisDownloadListCreator = DefaultSynopsisDownloadListCreator(testConfig)
                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://example4.com"),
                        URI("https://example5.com"),
                    ),
                    synopsis = Synopsis(
                        text = "text",
                        author = "me",
                    ),
                )
                Json.toJson(extensionData).writeToFile(tempDir.resolve("6f0e12caa76e9514.json"))

                // when
                val result = synopsisDownloadListCreator.createDownloadList()

                // then
                assertThat(result).isEmpty()
            }
        }
    }
}