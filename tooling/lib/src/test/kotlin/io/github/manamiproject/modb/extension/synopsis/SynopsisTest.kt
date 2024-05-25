package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

internal class SynopsisTest {

    @Nested
    inner class ConstructorTests {

        @Test
        fun `default value for lastUpdate is todays date`() {
            // given
            val today = LocalDate.now()

            // when
            val result = Synopsis(
                text = "text",
                author = "me",
            )

            // then
            assertThat(result.lastUpdatedAt).isEqualTo(today)
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "20240201"])
        fun `throws exception if lastUpdate is not a valid ISO date format`(input: String) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Synopsis(
                    text = "text",
                    author = "me",
                    lastUpdate = input,
                )
            }

            // then
            assertThat(result).hasMessage("Property 'lastUpdate' must be set and match ISO_LOCAL_DATE format.")
        }
    }

    @Nested
    inner class LastUpdatedAtTests {

        @Test
        fun `correctly parse value`() {
            // given
            val synopsis = Synopsis(
                text = "text",
                author = "me",
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