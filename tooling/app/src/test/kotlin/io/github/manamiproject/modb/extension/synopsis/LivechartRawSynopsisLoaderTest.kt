package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.livechart.LivechartConfig
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class LivechartRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/livechart/synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = LivechartRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${LivechartConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("At the tender age of 15, Kuroki Tomoko has already dated dozens and dozens of boys and she's easily the most popular girl around! The only problem is that absolutely none of that is real, and her perfect world exists only via dating games and romance shows. In fact, the sad truth is that she gets tongue tied just talking to people, and throughout middle school she's only had one actual friend. All of which makes Kuroki's entrance into the social pressure cooker of high school a new and special kind of hell. Because while Kuroki desperately wants to be popular, she's actually worse off than she would be if she was completely clueless as to how to go about it. After all, the things that work in \"otome\" games rarely play out the same way in reality, especially when the self-appointed \"leading lady\" isn't the paragon she thinks she is. There's not much gain and plenty of pain ahead, but even if it happens again and again, there's always someone else to blame in WATAMOTE ~ No Matter How I Look at It, It's You Guys' Fault I'm Not Popular!")
        }
    }

    @Test
    fun `successfully load synopsis - includes-source`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/livechart/includes-source.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = LivechartRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${LivechartConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Rimuru has officially become a Demon Lord after defeating Clayman. Following Walpurgis, the Demon Lords' banquet, Rimuru's domain is expanded to include the entire Great Forest of Jura. Anticipating a flood of representatives from all races showing up to pay their respects, Rimuru decides to throw a festival to commemorate the opening of Tempest, using it as an opportunity to gain new citizens and present Demon Lord Rimuru to the world. Meanwhile, in the Holy Empire of Lubelius, home base of the monster-hating cult of Luminism, Holy Knight Captain Hinata receives a message from Rimuru. But the message is actually a fabricated declaration of war sent by some unknown party. Upon learning that Hinata is heading for Tempest, Rimuru makes a decision... Thus begins a new challenge for Rimuru, striving to distinguish friend from foe in his pursuit of the ideal nation where humans and monsters can prosper together.")
        }
    }

    @Test
    fun `returns NoRawSynopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/livechart/no-synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = LivechartRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${LivechartConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}