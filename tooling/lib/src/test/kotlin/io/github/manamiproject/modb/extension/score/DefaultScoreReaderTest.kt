package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.extension.*
import io.github.manamiproject.modb.test.exceptionExpected
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

internal class DefaultScoreReaderTest {

    @Test
    fun `throws exception if sources is empty`() {
        runBlocking {
            // given
            val reader = DefaultScoreReader(
                origin = ModbExtensionRepoOrigin,
                fileAccessor = TestFileAccessor,
            )

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                reader.findScore(emptySet())
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

            val reader = DefaultScoreReader(
                origin = ModbExtensionRepoOrigin,
                fileAccessor = testFileAccessor,
            )

            val testSources = setOf(
                URI("https://example.org/data/2jk4h5.json"),
            )

            // when
            val result = reader.findScore(testSources)

            // then
            assertThat(result).isEqualTo(ScoreNotFound)
        }
    }

    @Test
    fun `correctly returns entry`() {
        runBlocking {
            // given
            val testSources = listOf(
                URI("https://example.org/data/2jk4h5.json"),
            )

            val testScore = Score(
                arithmeticMean = 1.0,
                arithmeticGeometricMean = 2.0,
                median = 3.0,
            )

            val testFileAccessor = object: FileAccessor by TestFileAccessor {
                override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue {
                    return ExtensionData(
                        sources = testSources,
                        score = testScore,
                    )
                }
            }

            val reader = DefaultScoreReader(
                origin = ModbExtensionRepoOrigin,
                fileAccessor = testFileAccessor,
            )

            // when
            val result = reader.findScore(testSources)

            // then
            assertThat(result).isEqualTo(testScore)
        }
    }
}