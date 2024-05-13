package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI
import kotlin.io.path.createFile
import kotlin.test.Test

internal class OriginKtTest {

    @Nested
    inner class LocalFileOriginTests {

        @Nested
        inner class ConstructorTests {

            @Test
            fun `throws exception if path doesn't exist`() {
                tempDirectory {
                    // given
                    val path = tempDir.resolve("unknown")

                    // when
                    val result = exceptionExpected<IllegalArgumentException> {
                        LocalFileOrigin(path)
                    }

                    // then
                    assertThat(result).hasMessage("Path [${path.toAbsolutePath()}] doesn't exist or is not a directory.")
                }
            }

            @Test
            fun `throws exception if path exists, but is not a directory`() {
                tempDirectory {
                    // given
                    val path = tempDir.resolve("test.txt").createFile()

                    // when
                    val result = exceptionExpected<IllegalArgumentException> {
                        LocalFileOrigin(path)
                    }

                    // then
                    assertThat(result).hasMessage("Path [${path.toAbsolutePath()}] doesn't exist or is not a directory.")
                }
            }
        }

        @Nested
        inner class ResolveTests {

            @ParameterizedTest
            @ValueSource(strings = ["", " "])
            fun `throws exception if filename is blank`(input: String) {
                tempDirectory {
                    // given
                    val origin = LocalFileOrigin(tempDir)

                    // when
                    val result = exceptionExpected<IllegalArgumentException> {
                        origin.resolve(input)
                    }

                    // then
                    assertThat(result).hasMessage("Filename must not be blank.")
                }
            }

            @Test
            fun `correctly resolve file for URI without trailing slash`() {
                tempDirectory {
                    // given
                    val filename = "049073efcafb1e52.json"
                    val origin = LocalFileOrigin(tempDir)

                    // when
                    val result = origin.resolve(filename)

                    // then
                    assertThat(result).isEqualTo(tempDir.resolve(filename))
                }
            }

            @Test
            fun `correctly resolve file for URI with trailing slash`() {
                tempDirectory {
                    // given
                    val filename = "049073efcafb1e52.json"
                    val origin = LocalFileOrigin(tempDir)

                    // when
                    val result = origin.resolve(filename)

                    // then
                    assertThat(result).isEqualTo(tempDir.resolve(filename))
                }
            }
        }
    }

    @Nested
    inner class ModbExtensionRepoOriginTest {

        @Nested
        inner class ResolveTests {

            @ParameterizedTest
            @ValueSource(strings = ["", " "])
            fun `throws exception if filename is blank`(input: String) {
                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    ModbExtensionRepoOrigin.resolve(input)
                }

                // then
                assertThat(result).hasMessage("Filename must not be blank.")
            }

            @Test
            fun `correctly resolve filename`() {
                // given
                val filename = "049073efcafb1e52.json"

                // when
                val result = ModbExtensionRepoOrigin.resolve(filename)

                // then
                assertThat(result).isEqualTo(URI("https://raw.githubusercontent.com/manami-project/modb-extension/main/data/$filename"))
            }
        }
    }

    @Nested
    inner class UriOriginTests {

        @Nested
        inner class ResolveTests {

            @ParameterizedTest
            @ValueSource(strings = ["", " "])
            fun `throws exception if filename is blank`(input: String) {
                tempDirectory {
                    // given
                    val origin = UriOrigin(URI("https://example.org/data"))

                    // when
                    val result = exceptionExpected<IllegalArgumentException> {
                        origin.resolve(input)
                    }

                    // then
                    assertThat(result).hasMessage("Filename must not be blank.")
                }
            }

            @Test
            fun `correctly resolve file for URI without trailing slash`() {
                // given
                val filename = "049073efcafb1e52.json"
                val origin = UriOrigin(URI("https://example.org/files"))

                // when
                val result = origin.resolve(filename)

                // then
                assertThat(result).isEqualTo(URI("https://example.org/files/049073efcafb1e52.json"))
            }

            @Test
            fun `correctly resolve file for URI with trailing slash`() {
                // given
                val filename = "049073efcafb1e52.json"
                val origin = UriOrigin(URI("https://example.org/files/"))

                // when
                val result = origin.resolve(filename)

                // then
                assertThat(result).isEqualTo(URI("https://example.org/files/049073efcafb1e52.json"))
            }
        }
    }
}