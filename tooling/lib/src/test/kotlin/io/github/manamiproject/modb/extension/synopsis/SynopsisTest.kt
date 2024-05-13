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
        fun `default values for created and lastUpdate are todays date`() {
            // given
            val today = LocalDate.now()

            // when
            val result = Synopsis(
                text = "text",
                author = "me",
                hash = "abcdtest",
            )

            // then
            assertThat(result.createdAt).isEqualTo(today)
            assertThat(result.lastUpdatedAt).isEqualTo(today)
        }

        @Test
        fun `default value for coAuthors is emptySet`() {
            // when
            val result = Synopsis(
                text = "text",
                author = "me",
                hash = "abcdtest",
            )

            // then
            assertThat(result.coAuthors).isEqualTo(emptySet<String>())
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "20240201"])
        fun `throws exception if created is not a valid ISO date format`(input: String) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Synopsis(
                    text = "text",
                    author = "me",
                    hash = "abcdtest",
                    created = input,
                )
            }

            // then
            assertThat(result).hasMessage("Property 'created' must be set and match ISO_LOCAL_DATE format.")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "20240201"])
        fun `throws exception if lastUpdate is not a valid ISO date format`(input: String) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Synopsis(
                    text = "text",
                    author = "me",
                    hash = "abcdtest",
                    lastUpdate = input,
                )
            }

            // then
            assertThat(result).hasMessage("Property 'lastUpdate' must be set and match ISO_LOCAL_DATE format.")
        }

        @Test
        fun `throws exception if created is after lastUpdate`() {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Synopsis(
                    text = "text",
                    author = "me",
                    hash = "abcdtest",
                    created = "2024-03-15",
                    lastUpdate = "2024-03-14",
                )
            }

            // then
            assertThat(result).hasMessage("Propterty 'created' cannot have a date before property 'lastUpdate'.")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " "])
        fun `throws exception if coAuthors contains blank entries`(input: String) {
            // given
            val testCoAuthors = setOf(
                "author1",
                input,
                "author3",
            )

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Synopsis(
                    text = "text",
                    author = "me",
                    coAuthors = testCoAuthors,
                    hash = "abcdtest",
                )
            }

            // then
            assertThat(result).hasMessage("Property 'coAuthors' must not contain blank entries.")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " "])
        fun `throws exception if hash is blank`(input: String) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                Synopsis(
                    text = "text",
                    author = "me",
                    hash = input,
                )
            }

            // then
            assertThat(result).hasMessage("Property 'hash' must not be blank.")
        }
    }

    @Nested
    inner class CreatedAtTests {

        @Test
        fun `correctly parse value`() {
            // given
            val synopsis = Synopsis(
                text = "text",
                author = "me",
                hash = "abcdtest",
                created = "2023-08-31",
            )

            // when
            val result = synopsis.createdAt

            // then
            assertThat(result).hasYear(2023)
            assertThat(result).hasMonthValue(8)
            assertThat(result).hasDayOfMonth(31)
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
                hash = "abcdtest",
                created = "2023-08-31",
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