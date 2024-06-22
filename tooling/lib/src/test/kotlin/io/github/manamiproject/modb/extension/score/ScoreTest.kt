package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate
import kotlin.test.Test

internal class ScoreTest {

    @Nested
    inner class ConstructorTests {

        @Test
        fun `throws exception if lastUpdate is not a valid ISO date format`() {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Score(
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
            val result = Score()

            // then
            assertThat(result.lastUpdatedAt).isEqualTo(today)
        }

        @Test
        fun `default value for all score values is 0`() {
            // when
            val result = Score()

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
                )
            }

            // then
            assertThat(result).hasMessage("Property 'median' must be within range 0.0 - 10.0.")
        }
    }

    @Nested
    inner class LastUpdatedAt {

        @Test
        fun `correctly parse value`() {
            // given
            val synopsis = Score(
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