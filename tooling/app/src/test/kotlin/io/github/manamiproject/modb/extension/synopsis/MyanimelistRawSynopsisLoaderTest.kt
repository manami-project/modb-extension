package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.mal.MalConfig
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class MyanimelistRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/myanimelist/synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = MyanimelistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${MalConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("A planet inhabited by creatures that never see the light of day. There's nothing to be proud of, but that's okay.")
        }
    }

    @Test
    fun `successfully load synopsis - includes-source`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/myanimelist/includes-source.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = MyanimelistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${MalConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("High schooler Minami Aoba has no strengths or aspirations and worries that she will graduate as a mere \"villager A.\" In search of her uniqueness, she comes across a golf driving range and is approached by the part-timer and genius golfer Haruka Akane. That is Minami's first encounter with golf. Together with the Haruka and influencer Ayaka Hoshimi, the unremarkable Minami aims for the moment when she becomes a protagonist.")
        }
    }

    @Test
    fun `successfully load synopsis - written-by`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/myanimelist/written-by.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = MyanimelistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${MalConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("For 15-year-old Uka Ishimori, middle school was the toughest experience she lived through. She was dubbed \"Rocky\" due to always freezing up in front of other people and was endlessly bullied as a result. Now in high school, she has a chance for a fresh start and to improve herself. But during her first week, she is accidentally soaked in juice by her classmate Kai Miura, a flashy guy with lemon-colored hair. On paper, Kai is everything that Uka lacks—he is popular, aloof, and bold enough to speak his mind. Despite their contrasting personalities, Kai encourages her to slowly come out of her shell, offering a helping hand when others avoided to do so. Although Uka initially wanted to forge friendships, she has realized her true feelings—she has fallen in love with Kai. What started with a lemon soda is about to change Uka and Kai's lives forever.")
        }
    }

    @Test
    fun `returns NoRawSynopsis - season-info`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/myanimelist/season-info.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = MyanimelistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${MalConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }

    @Test
    fun `returns NoRawSynopsis - third-part`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/myanimelist/season-info.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = MyanimelistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${MalConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }

    @Test
    fun `returns NoRawSynopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/myanimelist/no-synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = MyanimelistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${MalConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}