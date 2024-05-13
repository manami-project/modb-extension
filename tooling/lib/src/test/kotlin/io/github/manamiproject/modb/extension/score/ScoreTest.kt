package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

internal class ScoreTest {

    @Nested
    inner class ConstructorTests {

        @Test
        fun `throws exception if lastUpdate is not a valid ISO date format`() {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Score(
                    hash = "abcdtest",
                    lastUpdate = "20240101",
                )
            }

            assertThat(result).hasMessage("Property 'lastUpdate' must be set and match ISO_LOCAL_DATE format.")
        }

        @Test
        fun `default value for lastUpdate is todays date`() {
            // given
            val today = LocalDate.now()

            // when
            val result = Score(
                hash = "abcdtest",
            )

            // then
            assertThat(result.lastUpdatedAt).isEqualTo(today)
        }

        @Test
        fun `default value for all score values is 0`() {
            // when
            val result = Score(
                hash = "abcdtest",
            )

            // then
            assertThat(result.arithmeticMean).isZero()
            assertThat(result.arithmeticGeometricMean).isZero()
            assertThat(result.median).isZero()
        }

        @ParameterizedTest
        @ValueSource(doubles = [-0.1, 10.1])
        fun `throws error if arithmeticMean is out of range`(input: Double) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Score(
                    arithmeticMean = input,
                    hash = "abcdtest",
                )
            }

            // then
            assertThat(result).hasMessage("Property 'arithmeticMean' must be within range 0.0 - 10.0.")
        }

        @ParameterizedTest
        @ValueSource(doubles = [-0.1, 10.1])
        fun `throws error if arithmeticGeometricMean is out of range`(input: Double) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Score(
                    arithmeticGeometricMean = input,
                    hash = "abcdtest",
                )
            }

            // then
            assertThat(result).hasMessage("Property 'arithmeticGeometricMean' must be within range 0.0 - 10.0.")
        }

        @ParameterizedTest
        @ValueSource(doubles = [-0.1, 10.1])
        fun `throws error if median is out of range`(input: Double) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Score(
                    median = input,
                    hash = "abcdtest",
                )
            }

            // then
            assertThat(result).hasMessage("Property 'median' must be within range 0.0 - 10.0.")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " "])
        fun `throws exception if hash is blank`(input: String) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Score(
                    hash = input,
                )
            }

            // then
            assertThat(result).hasMessage("Property 'hash' must not be blank.")
        }
    }

    @Nested
    inner class LastUpdatedAt {

        @Test
        fun `correctly parse value`() {
            // given
            val synopsis = Score(
                hash = "abcdtest",
                lastUpdate = "2023-08-31",
            )

            // when
            val result = synopsis.lastUpdatedAt

            // then
            assertThat(result).hasYear(2023)
            assertThat(result).hasMonthValue(8)
            assertThat(result).hasDayOfMonth(31)
        }
    }
}