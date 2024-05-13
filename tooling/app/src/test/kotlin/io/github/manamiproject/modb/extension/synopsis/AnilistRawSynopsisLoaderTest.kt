package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.anilist.AnilistConfig
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class AnilistRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/synopsis.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("From classmates to brother and sister, living under the same roof. After his father's remarriage, Asamura Yuuta ends up getting a new stepsister, coincidentally the number one beauty of the school year, Ayase Saki. Having learned important values when it comes to man-woman relationships through the previous ones of their parents, they promise each other not to be too close, not to be too opposing, and to merely keep a vague and comfortable distance. On one hand, Saki, who has worked in solitude for the sake of her family, doesn't know how to properly rely on others, whereas Yuta is unsure of how to really treat her. Standing on fairly equal ground, these two slowly learn the comfortable sensation of living together. Their relationship slowly evolves from being strangers the more the days pass. Eventually, this could end up in a story about love for all we know.")
        }
    }

    @Test
    fun `successfully load synopsis - includes-adaption-source-note`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/includes-adaption-source-note.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("The Hashira, the Demon Slayer Corps' highest ranking swordsmen and members. The Hashira Training has begun in order to face the forthcoming battle against Muzan Kibutsuji. Each with their own thoughts and hopes held in their hearts, a new story for Tanjiro and the Hashira begins.")
        }
    }

    @Test
    fun `successfully load synopsis - includes-season-and-source`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/includes-season-and-source.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("After Makoto Misumi defeats Mitsurugi and Sofia Bulga, humanity is saved from the attacking demon army—for the time being. The goddess is aware of Makoto’s growing power, and she sees him as less of a nuisance and more of a rival. Makoto continues his journey to further expand his community of outcasts and connect with more hyumans. But will he be strong enough to hold off the coming storm?")
        }
    }

    @Test
    fun `successfully load synopsis - includes-source-and-note`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/includes-source-and-note.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("A boy fights... for \"the right death.\" Hardship, regret, shame: the negative feelings that humans feel become Curses that lurk in our everyday lives. The Curses run rampant throughout the world, capable of leading people to terrible misfortune and even death. What's more, the Curses can only be exorcised by another Curse. Itadori Yuji is a boy with tremendous physical strength, though he lives a completely ordinary high school life. One day, to save a friend who has been attacked by Curses, he eats the finger of the Double-Faced Specter, taking the Curse into his own soul. From then on, he shares one body with the Double-Faced Specter. Guided by the most powerful of sorcerers, Gojou Satoru, Itadori is admitted to the Tokyo Metropolitan Technical High School of Sorcery, an organization that fights the Curses... and thus begins the heroic tale of a boy who became a Curse to exorcise a Curse, a life from which he could never turn back.")
        }
    }

    @Test
    fun `successfully load synopsis - includes-sources`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/includes-source.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo(""""My wish as champion is for you to descend the tower and be my wife." Climbing a deadly tower, Oscar seeks the power of its master, the Witch of the Azure Moon. He hopes her incredible magic can break a curse that will kill any woman he takes for a wife. When the prince sees how beautiful Tinasha is, though, he has a better idea-since she's surely strong enough to survive his curse, she should just marry him instead! Tinasha isn't keen on the idea, but agrees to live with Oscar in the royal castle for a year while researching the spell placed on the prince. The witch's pretty face hides several lifetimes of dark secrets, however-secrets that begin resurfacing...""")
        }
    }

    @Test
    fun `successfully load synopsis - season-info`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/season-info.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }

    @Test
    fun `successfully load synopsis - season-info-and-note`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/season-info-and-note.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }

    @Test
    fun `successfully load synopsis - specially-long-note`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/specially-long-note.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Gold Roger was known as the Pirate King, the strongest and most infamous being to have sailed the Grand Line. The capture and death of Roger by the World Government brought a change throughout the world. His last words before his death revealed the location of the greatest treasure in the world, One Piece. It was this revelation that brought about the Grand Age of Pirates, men who dreamed of finding One Piece (which promises an unlimited amount of riches and fame), and quite possibly the most coveted of titles for the person who found it, the title of the Pirate King. Enter Monkey D. Luffy, a 17-year-old boy that defies your standard definition of a pirate. Rather than the popular persona of a wicked, hardened, toothless pirate who ransacks villages for fun, Luffy’s reason for being a pirate is one of pure wonder; the thought of an exciting adventure and meeting new and intriguing people, along with finding One Piece, are his reasons of becoming a pirate. Following in the footsteps of his childhood hero, Luffy and his crew travel across the Grand Line, experiencing crazy adventures, unveiling dark mysteries and battling strong enemies, all in order to reach One Piece.")
        }
    }

    @Test
    fun `returns NoRawSynopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anilist/no-synopsis.json")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnilistRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnilistConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}