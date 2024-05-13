package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.extension.score.Score
import io.github.manamiproject.modb.extension.synopsis.Synopsis
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.URL
import kotlin.io.path.createFile
import kotlin.io.path.writeText

internal class DefaultFileAccessorTest {

    @Nested
    inner class LoadEntryTests {

        @Test
        fun `successfully load file using LocalFileOrigin`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val content = """
                    {
                      "sources": [
                        "https://anidb.net/anime/4563",
                        "https://anilist.co/anime/1535",
                        "https://anime-planet.com/anime/death-note",
                        "https://anisearch.com/anime/3633",
                        "https://kitsu.io/anime/1376",
                        "https://livechart.me/anime/3437",
                        "https://myanimelist.net/anime/1535",
                        "https://notify.moe/anime/0-A-5Fimg"
                      ],
                      "synopsis": {
                        "text": "text-value",
                        "author": "author-value",
                        "coAuthors": [
                          "coAuthor-value1",
                          "coAuthor-value2"
                        ],
                        "hash": "049073efcafb1e52",
                        "created": "2024-01-01",
                        "lastUpdate": "2024-01-02"
                      },
                      "score": {
                        "arithmeticMean": 5.0,
                        "hash": "049073efcafb1e52",
                        "lastUpdate": "2024-01-03"
                      }
                    }
                """.trimIndent()

                content.writeToFile(tempDir.resolve("049073efcafb1e52.json").createFile())

                val extensionData = ExtensionData(
                    sources = sources,
                    Synopsis(
                        text = "text-value",
                        author = "author-value",
                        coAuthors = setOf(
                            "coAuthor-value1",
                            "coAuthor-value2",
                        ),
                        created = "2024-01-01",
                        hash = "049073efcafb1e52",
                        lastUpdate = "2024-01-02",
                    ),
                    score = Score(
                        arithmeticMean = 5.0,
                        hash = "049073efcafb1e52",
                        lastUpdate = "2024-01-03",
                    ),
                )

                val dir = LocalFileOrigin(tempDir)
                val fileAccessor = DefaultFileAccessor(
                    httpCient = TestHttpClient,
                )

                // when
                val result = fileAccessor.loadEntry(sources, dir)

                // then
                assertThat(result).isEqualTo(extensionData)
            }
        }

        @Test
        fun `return ExtensionDataNotFound using LocalFileOrigin, because file does not exist`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val dir = LocalFileOrigin(tempDir)
                val fileAccessor = DefaultFileAccessor(
                    httpCient = TestHttpClient,
                )

                // when
                val result = fileAccessor.loadEntry(sources, dir)

                // then
                assertThat(result).isEqualTo(ExtensionDataNotFound)
            }
        }

        @Test
        fun `return ExtensionDataNotFound using LocalFileOrigin, because file is empty`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                tempDir.resolve("049073efcafb1e52.json").createFile().writeText(EMPTY)
                val dir = LocalFileOrigin(tempDir)
                val fileAccessor = DefaultFileAccessor(
                    httpCient = TestHttpClient,
                )

                // when
                val result = fileAccessor.loadEntry(sources, dir)

                // then
                assertThat(result).isEqualTo(ExtensionDataNotFound)
            }
        }

        @Test
        fun `return ExtensionDataNotFound using ModbExtensionRepoOrigin, because response code is not 200`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val testHttpClient = object : HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse {
                        return HttpResponse(
                            code = 404,
                            body = "{}".toByteArray()
                        )
                    }
                }
                val fileAccessor = DefaultFileAccessor(
                    httpCient = testHttpClient,
                )

                // when
                val result = fileAccessor.loadEntry(sources, ModbExtensionRepoOrigin)

                // then
                assertThat(result).isEqualTo(ExtensionDataNotFound)
            }
        }

        @Test
        fun `return ExtensionDataNotFound using ModbExtensionRepoOrigin, because response is blank`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val testHttpClient = object : HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse {
                        return HttpResponse(
                            code = 200,
                            body = EMPTY.toByteArray()
                        )
                    }
                }
                val fileAccessor = DefaultFileAccessor(
                    httpCient = testHttpClient,
                )

                // when
                val result = fileAccessor.loadEntry(sources, ModbExtensionRepoOrigin)

                // then
                assertThat(result).isEqualTo(ExtensionDataNotFound)
            }
        }

        @Test
        fun `successfully load file using ModbExtensionRepoOrigin`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val extensionData = ExtensionData(
                    sources = sources,
                    Synopsis(
                        text = "text-value",
                        author = "author-value",
                        coAuthors = setOf(
                            "coAuthor-value1",
                            "coAuthor-value2",
                        ),
                        hash = "049073efcafb1e52",
                        created = "2024-01-01",
                        lastUpdate = "2024-01-02",
                    ),
                    score = Score(
                        arithmeticMean = 5.0,
                        hash = "049073efcafb1e52",
                        lastUpdate = "2024-01-03",
                    ),
                )

                val content = """
                    {
                      "sources": [
                        "https://anidb.net/anime/4563",
                        "https://anilist.co/anime/1535",
                        "https://anime-planet.com/anime/death-note",
                        "https://anisearch.com/anime/3633",
                        "https://kitsu.io/anime/1376",
                        "https://livechart.me/anime/3437",
                        "https://myanimelist.net/anime/1535",
                        "https://notify.moe/anime/0-A-5Fimg"
                      ],
                      "synopsis": {
                        "text": "text-value",
                        "author": "author-value",
                        "coAuthors": [
                          "coAuthor-value1",
                          "coAuthor-value2"
                        ],
                        "hash": "049073efcafb1e52",
                        "created": "2024-01-01",
                        "lastUpdate": "2024-01-02"
                      },
                      "score": {
                        "arithmeticMean": 5.0,
                        "hash": "049073efcafb1e52",
                        "lastUpdate": "2024-01-03"
                      }
                    }
                """.trimIndent()

                val testHttpClient = object : HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse {
                        return HttpResponse(
                            code = 200,
                            body = content.toByteArray(),
                        )
                    }
                }
                val fileAccessor = DefaultFileAccessor(
                    httpCient = testHttpClient,
                )

                // when
                val result = fileAccessor.loadEntry(sources, ModbExtensionRepoOrigin)

                // then
                assertThat(result).isEqualTo(extensionData)
            }
        }

        @Test
        fun `return ExtensionDataNotFound using UriOrigin, because response code is not 200`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val testHttpClient = object : HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse {
                        return HttpResponse(
                            code = 404,
                            body = "{}".toByteArray()
                        )
                    }
                }
                val fileAccessor = DefaultFileAccessor(
                    httpCient = testHttpClient,
                )
                val uriOrigin = UriOrigin(URI("https://example.org/data"))

                // when
                val result = fileAccessor.loadEntry(sources, uriOrigin)

                // then
                assertThat(result).isEqualTo(ExtensionDataNotFound)
            }
        }

        @Test
        fun `return ExtensionDataNotFound using UriOrigin, because response is blank`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val testHttpClient = object : HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse {
                        return HttpResponse(
                            code = 200,
                            body = EMPTY.toByteArray()
                        )
                    }
                }
                val fileAccessor = DefaultFileAccessor(
                    httpCient = testHttpClient,
                )
                val uriOrigin = UriOrigin(URI("https://example.org/data"))

                // when
                val result = fileAccessor.loadEntry(sources, uriOrigin)

                // then
                assertThat(result).isEqualTo(ExtensionDataNotFound)
            }
        }

        @Test
        fun `successfully load file using UriOrigin`() {
            tempDirectory {
                // given
                val sources = listOf(
                    URI("https://anidb.net/anime/4563"),
                    URI("https://anilist.co/anime/1535"),
                    URI("https://anime-planet.com/anime/death-note"),
                    URI("https://anisearch.com/anime/3633"),
                    URI("https://kitsu.io/anime/1376"),
                    URI("https://livechart.me/anime/3437"),
                    URI("https://myanimelist.net/anime/1535"),
                    URI("https://notify.moe/anime/0-A-5Fimg"),
                )

                val extensionData = ExtensionData(
                    sources = sources,
                    Synopsis(
                        text = "text-value",
                        author = "author-value",
                        coAuthors = setOf(
                            "coAuthor-value1",
                            "coAuthor-value2",
                        ),
                        hash = "049073efcafb1e52",
                        created = "2024-01-01",
                        lastUpdate = "2024-01-02",
                    ),
                    score = Score(
                        arithmeticMean = 5.0,
                        hash = "049073efcafb1e52",
                        lastUpdate = "2024-01-03",
                    ),
                )

                val content = """
                    {
                      "sources": [
                        "https://anidb.net/anime/4563",
                        "https://anilist.co/anime/1535",
                        "https://anime-planet.com/anime/death-note",
                        "https://anisearch.com/anime/3633",
                        "https://kitsu.io/anime/1376",
                        "https://livechart.me/anime/3437",
                        "https://myanimelist.net/anime/1535",
                        "https://notify.moe/anime/0-A-5Fimg"
                      ],
                      "synopsis": {
                        "text": "text-value",
                        "author": "author-value",
                        "coAuthors": [
                          "coAuthor-value1",
                          "coAuthor-value2"
                        ],
                        "hash": "049073efcafb1e52",
                        "created": "2024-01-01",
                        "lastUpdate": "2024-01-02"
                      },
                      "score": {
                        "arithmeticMean": 5.0,
                        "hash": "049073efcafb1e52",
                        "lastUpdate": "2024-01-03"
                      }
                    }
                """.trimIndent()

                val testHttpClient = object : HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse {
                        return HttpResponse(
                            code = 200,
                            body = content.toByteArray(),
                        )
                    }
                }
                val fileAccessor = DefaultFileAccessor(
                    httpCient = testHttpClient,
                )
                val uriOrigin = UriOrigin(URI("https://example.org/data"))

                // when
                val result = fileAccessor.loadEntry(sources, uriOrigin)

                // then
                assertThat(result).isEqualTo(extensionData)
            }
        }

        @Test
        fun `throws exception if sources is empty`() {
            tempDirectory {
                // given
                val fileAccessor = DefaultFileAccessor(
                    httpCient = TestHttpClient,
                )

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    fileAccessor.loadEntry(emptySet(), ModbExtensionRepoOrigin)
                }

                // then
                assertThat(result).hasMessage("Sources must not be empty.")
            }
        }
    }

    @Nested
    inner class SaveEntryTests {

        @Test
        fun `correctly create file without properties`() {
            tempDirectory {
                // given
                val fileAccessor = DefaultFileAccessor(
                    httpCient = TestHttpClient,
                )
                val dir = LocalFileOrigin(tempDir)

                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://anidb.net/anime/4563"),
                        URI("https://anilist.co/anime/1535"),
                        URI("https://anime-planet.com/anime/death-note"),
                        URI("https://anisearch.com/anime/3633"),
                        URI("https://kitsu.io/anime/1376"),
                        URI("https://livechart.me/anime/3437"),
                        URI("https://myanimelist.net/anime/1535"),
                        URI("https://notify.moe/anime/0-A-5Fimg"),
                    ),
                )

                // when
                fileAccessor.saveEntry(dir, extensionData)

                // then
                val content = tempDir.resolve("049073efcafb1e52.json").readFile()
                assertThat(content).isEqualTo("""
                    {
                      "sources": [
                        "https://anidb.net/anime/4563",
                        "https://anilist.co/anime/1535",
                        "https://anime-planet.com/anime/death-note",
                        "https://anisearch.com/anime/3633",
                        "https://kitsu.io/anime/1376",
                        "https://livechart.me/anime/3437",
                        "https://myanimelist.net/anime/1535",
                        "https://notify.moe/anime/0-A-5Fimg"
                      ]
                    }
                """.trimIndent())
            }
        }

        @Test
        fun `correctly create file with all properties`() {
            tempDirectory {
                // given
                val fileAccessor = DefaultFileAccessor(
                    httpCient = TestHttpClient,
                )
                val dir = LocalFileOrigin(tempDir)

                val extensionData = ExtensionData(
                    sources = listOf(
                        URI("https://anidb.net/anime/4563"),
                        URI("https://anilist.co/anime/1535"),
                        URI("https://anime-planet.com/anime/death-note"),
                        URI("https://anisearch.com/anime/3633"),
                        URI("https://kitsu.io/anime/1376"),
                        URI("https://livechart.me/anime/3437"),
                        URI("https://myanimelist.net/anime/1535"),
                        URI("https://notify.moe/anime/0-A-5Fimg"),
                    ),
                    Synopsis(
                        text = "text-value",
                        author = "author-value",
                        coAuthors = setOf(
                            "coAuthor-value1",
                            "coAuthor-value2",
                        ),
                        hash = "049073efcafb1e52",
                        created = "2024-01-01",
                        lastUpdate = "2024-01-02",
                    ),
                    score = Score(
                        arithmeticMean = 5.0,
                        hash = "049073efcafb1e52",
                        lastUpdate = "2024-01-03",
                    ),
                )

                // when
                fileAccessor.saveEntry(dir, extensionData)

                // then
                val content = tempDir.resolve("049073efcafb1e52.json").readFile()
                assertThat(content).isEqualTo("""
                    {
                      "sources": [
                        "https://anidb.net/anime/4563",
                        "https://anilist.co/anime/1535",
                        "https://anime-planet.com/anime/death-note",
                        "https://anisearch.com/anime/3633",
                        "https://kitsu.io/anime/1376",
                        "https://livechart.me/anime/3437",
                        "https://myanimelist.net/anime/1535",
                        "https://notify.moe/anime/0-A-5Fimg"
                      ],
                      "synopsis": {
                        "text": "text-value",
                        "author": "author-value",
                        "coAuthors": [
                          "coAuthor-value1",
                          "coAuthor-value2"
                        ],
                        "hash": "049073efcafb1e52",
                        "created": "2024-01-01",
                        "lastUpdate": "2024-01-02"
                      },
                      "score": {
                        "arithmeticMean": 5.0,
                        "arithmeticGeometricMean": 0.0,
                        "median": 0.0,
                        "hash": "049073efcafb1e52",
                        "lastUpdate": "2024-01-03"
                      }
                    }
                """.trimIndent())
            }
        }
    }
}