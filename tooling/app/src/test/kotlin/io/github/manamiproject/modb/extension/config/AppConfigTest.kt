package io.github.manamiproject.modb.extension.config

import io.github.manamiproject.modb.core.config.ConfigRegistry
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.extension.TestConfigRegistry
import io.github.manamiproject.modb.extension.TestMetaDataProviderConfig
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import java.net.URI
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile

internal class AppConfigTest {

    @Nested
    inner class DataDirectoryTests {

        @Test
        fun `throws exception if directory doesn't exist`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String = "pathNotExists"
            }
            val appConfig = AppConfig(
                configRegistry = testConfigRegistry,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                appConfig.dataDirectory()
            }

            // then
            assertThat(result).hasMessage("Given value for dataDirectory [pathNotExists] doesn't exist or is not a directory.")
        }

        @Test
        fun `throws exception if path is not a directory`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("testfile.txt").createFile()

                val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                    override fun string(key: String): String = file.toAbsolutePath().toString()
                }

                val appConfig = AppConfig(
                    configRegistry = testConfigRegistry,
                )

                // when
                val result = exceptionExpected<IllegalStateException> {
                    appConfig.dataDirectory()
                }

                // then
                assertThat(result).hasMessageEndingWith("/testfile.txt] doesn't exist or is not a directory.")
            }
        }

        @Test
        fun `use config value`() {
            tempDirectory {
                // given
                val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                    override fun string(key: String): String = tempDir.toString()
                }

                val appConfig = AppConfig(
                    configRegistry = testConfigRegistry,
                )

                // when
                val result = appConfig.dataDirectory()

                // then
                assertThat(result).isEqualTo(tempDir)
            }
        }
    }

    @Nested
    inner class AnimeDatasetTests {

        @Test
        fun `use config value`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String = "http://localhost:8080/anime-offline-database.zip"
            }
            val config = AppConfig(
                configRegistry = testConfigRegistry,
            )

            // when
            val result = config.animeDataSet()

            // then
            assertThat(result).isEqualTo(URI("http://localhost:8080/anime-offline-database.zip"))
        }

        @Test
        fun `default value`() {
            // given
            val config = AppConfig()

            // when
            val result = config.animeDataSet()

            // then
            assertThat(result).isEqualTo(URI("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database.zip"))
        }
    }

    @Nested
    inner class RawFilesDirectoryTests {

        @Test
        fun `throws exception if directory doesn't exist`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String = "pathNotExists"
            }
            val appConfig = AppConfig(
                configRegistry = testConfigRegistry,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                appConfig.rawFilesDirectory()
            }

            // then
            assertThat(result).hasMessage("Given value for rawFilesDirectory [pathNotExists] doesn't exist or is not a directory.")
        }

        @Test
        fun `throws exception if path is not a directory`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("testfile.txt").createFile()

                val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                    override fun string(key: String): String = file.toAbsolutePath().toString()
                }

                val appConfig = AppConfig(
                    configRegistry = testConfigRegistry,
                )

                // when
                val result = exceptionExpected<IllegalStateException> {
                    appConfig.rawFilesDirectory()
                }

                // then
                assertThat(result).hasMessageEndingWith("/testfile.txt] doesn't exist or is not a directory.")
            }
        }

        @Test
        fun `use config value`() {
            tempDirectory {
                // given
                val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                    override fun string(key: String): String = tempDir.toString()
                }

                val appConfig = AppConfig(
                    configRegistry = testConfigRegistry,
                )

                // when
                val result = appConfig.rawFilesDirectory()

                // then
                assertThat(result).isEqualTo(tempDir)
            }
        }

        @Test
        fun `uses an existing meta data provider specific directory`() {
            tempDirectory {
                // given
                val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                    override fun string(key: String): String = tempDir.toString()
                }

                val testMetaDataProviderConfig = object: MetaDataProviderConfig by TestMetaDataProviderConfig {
                    override fun hostname(): Hostname = "example.org"
                }

                tempDir.resolve(testMetaDataProviderConfig.hostname()).createDirectory()

                val appConfig = AppConfig(
                    configRegistry = testConfigRegistry,
                )

                // when
                val result = appConfig.rawFilesDirectory(testMetaDataProviderConfig)

                // then
                assertThat(result).exists()
            }
        }

        @Test
        fun `creates a meta data provider specific directory if it doesn't exist`() {
            tempDirectory {
                // given
                val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                    override fun string(key: String): String = tempDir.toString()
                }

                val testMetaDataProviderConfig = object: MetaDataProviderConfig by TestMetaDataProviderConfig {
                    override fun hostname(): Hostname = "example.org"
                }

                val appConfig = AppConfig(
                    configRegistry = testConfigRegistry,
                )

                // when
                val result = appConfig.rawFilesDirectory(testMetaDataProviderConfig)

                // then
                assertThat(result).exists()
            }
        }
    }
}