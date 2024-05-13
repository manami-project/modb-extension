package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.anidb.AnidbConfig
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class AnidbRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("A music video for the song Monster Generation, sung by the Idolish 7, an idol group from Idolish 7 (アイドリッシュセブン) media-mix project.")
        }
    }

    @Test
    fun `successfully load synopsis - based-on-without-named-prefix`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/based-on-without-named-prefix.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("""Seihikari Genki and his friends join the adventure in order to save seven gods of the White Galaxy.""")
        }
    }

    @Test
    fun `successfully load synopsis - based-on_source_note`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/based-on_source_note.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("""In order to save her brother and nephew, who have been kidnapped by a mysterious religious group known as the Genuine Love Society, Juri and her family cast a spell using a stone hidden by her grandfather to enter the world of stopped-time known as Stasis. However, when they infiltrate the kidnapper's base, they're met by other people who can also move about freely. With grotesque creatures lurking about, will they be able to escape the parallel world and return to their normal lives?!""")
        }
    }

    @Test
    fun `successfully load synopsis - description-by`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/description-by.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("""10 years since the great war, tensions were mounting within the country of Wellber, which was barely capable of keeping peace, as war could commence at any time with its neighbouring country, Sangatras. In order to avoid warfare, the king of Wellber, Haidel planned on marrying off his daughter, Princess Rita, to Sangatras' Prince Guernia. However, Rita stabbed her groom to be and ran away. Infuriated, Sangatras' King Ranbahnhof threatens to wage war unless Rita is captured and publicly executed within 14 days. In order to avoid the worst case scenario, Rita decides to head for the neutral country of Greedom. Meanwhile, the woman thief Tina sneaks into Castle Wellber, seeking its treasures, when she happens to witness the stabbing of Guernia by Rita. Whether it be by coincidence or necessity, Tina receives information that the Wasp Man she was after is in Greedom, her sworn enemy who took the life of her parents. Tina agrees to become Rita's bodyguard as they head out to Greedom. Shouldering their fate and destiny, the two meet, leave, and set out on their journey. What awaits them is war or peace, vengeance or death...""")
        }
    }

    @Test
    fun `successfully load synopsis - multiple-notes`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/multiple-notes.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("""Five years after a mystifying disaster decimates cities across the globe, Morino Seria receives an invitation from a mysterious woman to join Alice in Theater, a small stage troupe that takes it upon themselves to brighten the world through their performances using 3D hologram technology. As Seria settles in, she begins to uncover unexpected truths about herself and the world around her...""")
        }
    }

    @Test
    fun `successfully load synopsis - translated-and-adapted`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/translated-and-adapted.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("""Hoshimiya Ichigo is a normal girl in her 1st year of middle-school. However, her life changes drastically when, at her good friend Aoi's invitation, she is admitted into Starlight Academy, a famous school for grooming idols. In the weeks and months to come, Ichigo meets many rivals, learns the skills of being an idol, and, using her Aikatsu! card, enters numerous auditions.""")
        }
    }

    @Test
    fun `successfully load synopsis - written-by`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/written-by.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("""Kujou Sakurako is a beautiful and talented osteologist, or bone specialist, whose life is centered around bones only. Sakurako has little tolerance for illogical behavior and a lot of trouble understanding other people; she would be isolated in her study full of skeletons if not for her assistant, high school boy Tatewaki Shoutarou, who does his best to drag her out and make her interact with others. And yet, whenever they go out, the dead seem to be around the corner, because the two of them often stumble upon human skeletons or corpses.""")
        }
    }

    @Test
    fun `successfully load synopsis - reduced-to-zero`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anidb/reduced-to-zero.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

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
                        loadTestResource("synopsis/anidb/no-synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnidbRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnidbConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}