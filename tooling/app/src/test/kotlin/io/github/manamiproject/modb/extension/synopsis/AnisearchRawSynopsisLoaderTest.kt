package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.anidb.AnidbConfig
import io.github.manamiproject.modb.anisearch.AnisearchConfig
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class AnisearchRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anisearch/synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnisearchRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnisearchConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("In ancient Asia lives a young woman named Maomao, who ekes out a living as a pharmacist in the red-light district and is always thinking about making medicine to earn her daily bread. Her goal is to eventually find a vocation where she can earn a lot of money with as little work as possible and enjoy a pleasant everyday life – but this is not to be granted to her! She is abducted from the red-light district and forced to work as a simple servant in the imperial palace. Of course, she does not like this at all, which is why she is already thinking of a plan to escape this awkward situation. By chance, she learns at this time that the emperor’s two sons are apparently suffering from a disease that no one is able to cure, which is why the self-proclaimed physician takes on this case in secret. Despite all temptations to remain anonymous, she is unmasked after only a short time by one of the two descendants – namely, Jinshi. He immediately recognises that the 17-year-old has a hidden talent and decides to employ her as his personal physician. In this new vocation, she now has to decipher all kinds of complicated diseases, far from all of which occur naturally. Will Maomao be able to solve all the medical mysteries and live the life she wants, or will her plans be thwarted?")
        }
    }

    @Test
    fun `returns NoRawSynopsis`() {
        runBlocking {
            // given
            val testDownloader = object: Downloader by TestDownloader {
                override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String {
                    return if (id == "1535") {
                        loadTestResource("synopsis/anisearch/no-synopsis.html")
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val scoreLoader = AnisearchRawSynopsisLoader(
                downloader = testDownloader,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${AnisearchConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}