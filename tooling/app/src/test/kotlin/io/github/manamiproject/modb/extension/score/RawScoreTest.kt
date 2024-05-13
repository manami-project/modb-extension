package io.github.manamiproject.modb.extension.score

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test


internal class RawScoreTest {

    @Nested
    inner class ScaledValueTests() {

        @ParameterizedTest
        @ValueSource(doubles = [1.0, 5.0 ,10.0])
        fun `correctly keep values as is if the range is already as expected`(input: Double) {
            // given
            val rawScore = RawScore(input, 1.0..10.0)

            // when
            val result = rawScore.scaledValue()

            // then
            assertThat(result).isEqualTo(input)
        }

        @Test
        fun `correctly rescale value 1 from 1 to 100 range`() {
            // given
            val rawScore = RawScore(1.0, 1.0..100.0)

            // when
            val result = rawScore.scaledValue()

            // then
            assertThat(result).isEqualTo(1.0)
        }

        @Test
        fun `correctly rescale value 5 from 1 to 100 range`() {
            // given
            val rawScore = RawScore(50.0, 1.0..100.0)

            // when
            val result = rawScore.scaledValue()

            // then
            assertThat(result).isEqualTo(5.454545454545455)
        }

        @Test
        fun `correctly rescale value 100 from 1 to 100 range`() {
            // given
            val rawScore = RawScore(100.0, 1.0..100.0)

            // when
            val result = rawScore.scaledValue()

            // then
            assertThat(result).isEqualTo(10.0)
        }

        @Test
        fun `correctly rescale value 1 from 1 to 5 range`() {
            // given
            val rawScore = RawScore(1.0, 1.0..5.0)

            // when
            val result = rawScore.scaledValue()

            // then
            assertThat(result).isEqualTo(1.0)
        }

        @Test
        fun `correctly rescale value 5 from 1 to 5 range`() {
            // given
            val rawScore = RawScore(3.5, 1.0..5.0)

            // when
            val result = rawScore.scaledValue()

            // then
            assertThat(result).isEqualTo(6.625)
        }

        @Test
        fun `correctly rescale value 100 from 1 to 5 range`() {
            // given
            val rawScore = RawScore(5.0, 1.0..5.0)

            // when
            val result = rawScore.scaledValue()

            // then
            assertThat(result).isEqualTo(10.0)
        }
    }
}