package io.github.manamiproject.modb.extension.updates

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.io.path.createFile

internal class DefaultUpdatableItemsFinderTest {

    @Nested
    inner class ConstructorTests {

        @Test
        fun `throws exception if directory doesn't exist`() {
            tempDirectory {
                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    DefaultUpdatableItemsFinder(tempDir.resolve("unknown"))
                }

                // then
                assertThat(result).hasMessage("Data directory either doesn't exist or is not a directory.")
            }
        }

        @Test
        fun `throws exception if path is not a directory`() {
            tempDirectory {
                // given
                val path = tempDir.resolve("text.txt").createFile()

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    DefaultUpdatableItemsFinder(path)
                }

                // then
                assertThat(result).hasMessage("Data directory either doesn't exist or is not a directory.")
            }
        }
    }

    @Nested
    inner class FindUpdateItems {

        @Test
        fun `correctly returns new db entries`() {
            tempDirectory {
                // given
                val updateItemsFinder = DefaultUpdatableItemsFinder(tempDir)
                val testDbContent = setOf(
                    hashSetOf(
                        URI("https://example1.org"),
                        URI("https://example2.org"),
                    )
                )

                // when
                val result = updateItemsFinder.findNewDbEntries(testDbContent, emptySet())

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    hashSetOf(
                        URI("https://example1.org"),
                        URI("https://example2.org"),
                    )
                )
            }
        }

        @Test
        fun `correctly returns updated db entries`() {
            tempDirectory {
                // given
                """
                    {
                      "sources": [
                        "https://example1.org",
                        "https://example2.org"
                      ]
                    }
                """.trimIndent().writeToFile(tempDir.resolve("5bab47db75e97361.json"))

                val updateItemsFinder = DefaultUpdatableItemsFinder(tempDir)
                val testDbContent = setOf(
                    hashSetOf(
                        URI("https://example1.org"),
                        URI("https://example2.org"),
                        URI("https://example3.org"),
                    )
                )

                // when
                val result = updateItemsFinder.findNewDbEntries(testDbContent, setOf("5bab47db75e97361.json"))

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    hashSetOf(
                        URI("https://example1.org"),
                        URI("https://example2.org"),
                        URI("https://example3.org"),
                    )
                )
            }
        }

        @Test
        fun `removes obsolete files`() {
            tempDirectory {
                // given
                val testFile = tempDir.resolve("5bab47db75e97361.json")
                """
                    {
                      "sources": [
                        "https://example1.org",
                        "https://example2.org"
                      ]
                    }
                """.trimIndent().writeToFile(testFile)

                val updateItemsFinder = DefaultUpdatableItemsFinder(tempDir)
                val testDbContent = emptySet<HashSet<URI>>()

                // when
                updateItemsFinder.findNewDbEntries(testDbContent, setOf("5bab47db75e97361.json"))

                // then
                assertThat(testFile).doesNotExist()
            }
        }

        @Test
        fun `removes files containing dead entries`() {
            tempDirectory {
                // given
                val testFile = tempDir.resolve("bcecb18f2e774d7c.json")
                """
                    {
                      "sources": [
                        "https://example1.org",
                        "https://example2.org",
                        "https://example3.org"
                      ]
                    }
                """.trimIndent().writeToFile(testFile)

                val updateItemsFinder = DefaultUpdatableItemsFinder(tempDir)
                val testDbContent = setOf(
                    hashSetOf(
                        URI("https://example1.org"),
                        URI("https://example2.org"),
                    )
                )

                // when
                updateItemsFinder.findNewDbEntries(testDbContent, setOf("bcecb18f2e774d7c.json"))

                // then
                assertThat(testFile).doesNotExist()
            }
        }

        @Test
        fun `removes outdated files and returns split entries as new`() {
            tempDirectory {
                // given
                val testFile = tempDir.resolve("a993e23d038feebc.json")
                """
                    {
                      "sources": [
                        "https://example1.org",
                        "https://example2.org",
                        "https://example3.org",
                        "https://example4.org"
                      ]
                    }
                """.trimIndent().writeToFile(testFile)

                val updateItemsFinder = DefaultUpdatableItemsFinder(tempDir)
                val testDbContent = setOf(
                    hashSetOf(
                        URI("https://example1.org"),
                        URI("https://example2.org"),
                    ),
                    hashSetOf(
                        URI("https://example3.org"),
                        URI("https://example4.org"),
                    ),
                )

                // when
                val result = updateItemsFinder.findNewDbEntries(testDbContent, setOf("a993e23d038feebc.json"))

                // then
                assertThat(testFile).doesNotExist()
                assertThat(result).containsExactlyInAnyOrder(
                    hashSetOf(
                        URI("https://example1.org"),
                        URI("https://example2.org"),
                    ),
                    hashSetOf(
                        URI("https://example3.org"),
                        URI("https://example4.org"),
                    ),
                )
            }
        }
    }
}