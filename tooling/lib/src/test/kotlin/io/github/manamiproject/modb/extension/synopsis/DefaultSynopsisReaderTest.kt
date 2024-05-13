package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.extension.*
import io.github.manamiproject.modb.test.exceptionExpected
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

internal class DefaultSynopsisReaderTest {

    @Test
    fun `throws exception if sources is empty`() {
        runBlocking {
            // given
            val reader = DefaultSynopsisReader(
                origin = ModbExtensionRepoOrigin,
                fileAccessor = TestFileAccessor,
            )

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                reader.findSynopsis(emptySet())
            }

            // then
            assertThat(result).hasMessage("Sources must not be empty.")
        }
    }

    @Test
    fun `returns not found`() {
        runBlocking {
            // given
            val testFileAccessor = object: FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue = ExtensionDataNotFound
            }

            val reader = DefaultSynopsisReader(
                origin = ModbExtensionRepoOrigin,
                fileAccessor = testFileAccessor,
            )

            val testSources = setOf(
                URI("https://example.org/data/2jk4h5.json"),
            )

            // when
            val result = reader.findSynopsis(testSources)

            // then
            assertThat(result).isEqualTo(SynopsisNotFound)
        }
    }

    @Test
    fun `correctly returns entry`() {
        runBlocking {
            // given
            val testSources = listOf(
                URI("https://example.org/data/2jk4h5.json"),
            )

            val testSynopsis = Synopsis(
                text = "text",
                author = "me",
                hash = "34962b38dbd33acf",
            )

            val testFileAccessor = object: FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue {
                    return ExtensionData(
                        sources = testSources,
                        synopsis = testSynopsis,
                    )
                }
            }

            val reader = DefaultSynopsisReader(
                origin = ModbExtensionRepoOrigin,
                fileAccessor = testFileAccessor,
            )

            // when
            val result = reader.findSynopsis(testSources)

            // then
            assertThat(result).isEqualTo(testSynopsis)
        }
    }
}