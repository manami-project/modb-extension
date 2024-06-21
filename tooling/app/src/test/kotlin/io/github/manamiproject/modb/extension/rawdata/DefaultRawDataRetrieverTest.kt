package io.github.manamiproject.modb.extension.rawdata

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.extension.TestConfig
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.extension.TestMetaDataProviderConfig
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test

internal class DefaultRawDataRetrieverTest {

    @Nested
    inner class RetrieveRawDataTests {

        @Test
        fun `download data if it doesn't exist`() {
            tempDirectory {
                // given
                val testDownloader = object: Downloader by TestDownloader {
                    override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String = "downloaded content"
                }

                val testMetaDataProviderConfig = object: MetaDataProviderConfig by TestMetaDataProviderConfig {
                    override fun hostname(): Hostname = "example.org"
                    override fun fileSuffix(): FileSuffix = "txt"
                }

                val testConfig = object: Config by TestConfig {
                    override fun rawFilesDirectory(metaDataProviderConfig: MetaDataProviderConfig): Directory = tempDir
                }

                val retriever = DefaultRawDataRetriever(
                    appConfig = testConfig,
                    metaDataProviderConfig = testMetaDataProviderConfig,
                    downloader = testDownloader,
                )

                // when
                retriever.retrieveRawData("1535")

                // then
                assertThat(tempDir.resolve("1535.txt").readFile()).isEqualTo("downloaded content")
            }
        }

        @Test
        fun `loads data from file if it exists`() {
            tempDirectory {
                // given
                val testMetaDataProviderConfig = object: MetaDataProviderConfig by TestMetaDataProviderConfig {
                    override fun hostname(): Hostname = "example.org"
                    override fun fileSuffix(): FileSuffix = "txt"
                }

                val testConfig = object: Config by TestConfig {
                    override fun rawFilesDirectory(metaDataProviderConfig: MetaDataProviderConfig): Directory = tempDir
                }

                val retriever = DefaultRawDataRetriever(
                    appConfig = testConfig,
                    metaDataProviderConfig = testMetaDataProviderConfig,
                    downloader = TestDownloader,
                )

                "existing file content".writeToFile(tempDir.resolve("1535.txt"))

                // when
                retriever.retrieveRawData("1535")

                // then
                assertThat(tempDir.resolve("1535.txt").readFile()).isEqualTo("existing file content")
            }
        }
    }
}