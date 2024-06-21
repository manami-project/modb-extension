package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.extension.TestConfig
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

internal class DefaultScoreCreatorTest {

    @Test
    fun `no score`() {
        runBlocking {
            // given
            val testRawScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = NoRawScore
            }

            val scoreCreator = DefaultScoreCreator(
                appConfig = TestConfig,
                rawScoreLoaders = mapOf(
                    "example.org" to testRawScoreLoader,
                )
            )

            val testSources = setOf(
                URI("https://example.org/data/1535"),
            )

            // when
            val result = scoreCreator.createScore(testSources)

            // then
            assertThat(result).isEqualTo(Score())
        }
    }

    @Test
    fun `return values as-is for a single raw score`() {
        runBlocking {
            // given
            val testRawScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(5.0, 1.0..10.0)
            }

            val scoreCreator = DefaultScoreCreator(
                appConfig = TestConfig,
                rawScoreLoaders = mapOf(
                    "example.org" to testRawScoreLoader,
                )
            )

            val testSources = setOf(
                URI("https://example.org/data/1535"),
            )

            // when
            val result = scoreCreator.createScore(testSources) as Score

            // then
            assertThat(result.arithmeticMean).isEqualTo(5.0)
            assertThat(result.arithmeticGeometricMean).isEqualTo(5.0)
            assertThat(result.median).isEqualTo(5.0)
        }
    }

    @Test
    fun `correctly calculate for two raw scores`() {
        runBlocking {
            // given
            val firstTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(3.14, 1.0..10.0)
            }

            val secondTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.85, 1.0..10.0)
            }

            val scoreCreator = DefaultScoreCreator(
                appConfig = TestConfig,
                rawScoreLoaders = mapOf(
                    "example1.org" to firstTestScoreLoader,
                    "example2.org" to secondTestScoreLoader,
                )
            )

            val testSources = setOf(
                URI("https://example1.org/data/1535"),
                URI("https://example2.org/data/1535"),
            )

            // when
            val result = scoreCreator.createScore(testSources) as Score

            // then
            assertThat(result.arithmeticMean).isEqualTo(5.495)
            assertThat(result.arithmeticGeometricMean).isEqualTo(5.226525514938778)
            assertThat(result.median).isEqualTo(5.495)
        }
    }

    @Test
    fun `correctly calculate for three raw scores`() {
        runBlocking {
            // given
            val firstTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(3.14, 1.0..10.0)
            }

            val secondTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.85, 1.0..10.0)
            }

            val thirdTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(6.19, 1.0..10.0)
            }

            val scoreCreator = DefaultScoreCreator(
                appConfig = TestConfig,
                rawScoreLoaders = mapOf(
                    "example1.org" to firstTestScoreLoader,
                    "example2.org" to secondTestScoreLoader,
                    "example3.org" to thirdTestScoreLoader,
                )
            )

            val testSources = setOf(
                URI("https://example1.org/data/1535"),
                URI("https://example2.org/data/1535"),
                URI("https://example3.org/data/1535"),
            )

            // when
            val result = scoreCreator.createScore(testSources) as Score

            // then
            assertThat(result.arithmeticMean).isEqualTo(5.726666666666667)
            assertThat(result.arithmeticGeometricMean).isEqualTo(5.533451026668135)
            assertThat(result.median).isEqualTo(6.19)
        }
    }

    @Test
    fun `correctly calculate for four raw scores`() {
        runBlocking {
            // given
            val firstTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(3.14, 1.0..10.0)
            }

            val secondTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.85, 1.0..10.0)
            }

            val thirdTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(6.19, 1.0..10.0)
            }

            val fourthTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(5.21, 1.0..10.0)
            }

            val scoreCreator = DefaultScoreCreator(
                appConfig = TestConfig,
                rawScoreLoaders = mapOf(
                    "example1.org" to firstTestScoreLoader,
                    "example2.org" to secondTestScoreLoader,
                    "example3.org" to thirdTestScoreLoader,
                    "example4.org" to fourthTestScoreLoader,
                )
            )

            val testSources = setOf(
                URI("https://example1.org/data/1535"),
                URI("https://example2.org/data/1535"),
                URI("https://example3.org/data/1535"),
                URI("https://example4.org/data/1535"),
            )

            // when
            val result = scoreCreator.createScore(testSources) as Score

            // then
            assertThat(result.arithmeticMean).isEqualTo(5.5975)
            assertThat(result.arithmeticGeometricMean).isEqualTo(5.452724485612647)
            assertThat(result.median).isEqualTo(5.7)
        }
    }

    @Test
    fun `use correct abort condition to prevent infinite loop`() {
        runBlocking {
            // given
            val firstTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.090909090909091, 1.0..10.0)
            }

            val secondTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.514545454545455, 1.0..10.0)
            }

            val thirdTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.67, 1.0..10.0)
            }

            val fourthTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.16, 1.0..10.0)
            }

            val fifthTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(5.95, 1.0..10.0)
            }

            val sixthTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.342, 1.0..10.0)
            }

            val seventhTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(6.9411764705882355, 1.0..10.0)
            }

            val eighthTestScoreLoader = object: RawScoreLoader by TestRawScoreLoader {
                override suspend fun loadRawScore(source: URI): RawScoreReturnValue = RawScore(7.27, 1.0..10.0)
            }

            val scoreCreator = DefaultScoreCreator(
                appConfig = TestConfig,
                rawScoreLoaders = mapOf(
                    "example1.org" to firstTestScoreLoader,
                    "example2.org" to secondTestScoreLoader,
                    "example3.org" to thirdTestScoreLoader,
                    "example4.org" to fourthTestScoreLoader,
                    "example5.org" to fifthTestScoreLoader,
                    "example6.org" to sixthTestScoreLoader,
                    "example7.org" to seventhTestScoreLoader,
                    "example8.org" to eighthTestScoreLoader,
                )
            )

            val testSources = setOf(
                URI("https://example1.org/data/1535"),
                URI("https://example2.org/data/1535"),
                URI("https://example3.org/data/1535"),
                URI("https://example4.org/data/1535"),
                URI("https://example5.org/data/1535"),
                URI("https://example6.org/data/1535"),
                URI("https://example7.org/data/1535"),
                URI("https://example8.org/data/1535"),
            )

            // when
            val result = scoreCreator.createScore(testSources) as Score

            // then
            assertThat(result.arithmeticMean).isEqualTo(7.117328877005347)
            assertThat(result.arithmeticGeometricMean).isEqualTo(7.108196705468187)
            assertThat(result.median).isEqualTo(7.215)
        }
    }
}