package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.extension.TestConfig
import io.github.manamiproject.modb.extension.TestDownloader
import io.github.manamiproject.modb.extension.TestRawDataRetriever
import io.github.manamiproject.modb.extension.rawdata.RawDataRetriever
import io.github.manamiproject.modb.notify.NotifyConfig
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class NotifyRawSynopsisLoaderTest {

    @Test
    fun `successfully load synopsis`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/notify/synopsis.json")
            }

            val scoreLoader = NotifyRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${NotifyConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Zagan is feared by the masses as an evil sorcerer. Both socially awkward and foulmouthed, he spends his days studying sorcery while beating down any trespassers within his domain. One day he's invited to a dark auction, and what he finds there is an elven slave girl of peerless beauty, Nephy. Having fallen in love at first sight, Zagan uses up his entire fortune to purchase her, but being a poor conversationalist, he has no idea how to properly interact with her. Thus, the awkward cohabitation of a sorcerer who has no idea how to convey his love and his slave who yearns for her master but has no idea how to appeal to him begins.")
        }
    }

    @Test
    fun `successfully load synopsis - including-source`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/notify/including-source.json")
            }

            val scoreLoader = NotifyRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${NotifyConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Middle school left Uka Ishimori with nothing but scars—to the point where she’s forgotten how to laugh or cry or even say “hello.” But a chance reencounter with a boy with lemon-colored hair invigorates her, giving her hope that maybe, just maybe, life can be that much sweeter if she finally reaches out for help.")
        }
    }

    @Test
    fun `successfully load synopsis - including-source-no-brackets`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/notify/including-source-no-brackets.json")
            }

            val scoreLoader = NotifyRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${NotifyConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Lawrence is a traveling merchant selling various goods from a horse-drawn cart. One day, he arrives at a village and meets a beautiful girl with the ears and tail of an animal! Her name is Holo the Wisewolf and she brings bountiful harvests. She wishes to return to her homeland, and Lawrence offers to take her. Now, the once-lonely merchant and the once-lonely wisewolf begin their journey north.")
        }
    }

    @Test
    fun `successfully load synopsis - written-by`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/notify/written-by.json")
            }

            val scoreLoader = NotifyRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${NotifyConfig.hostname()}/anime/1535"))

            // then
            assertThat((result as RawSynopsis).text).isEqualTo("Although everything the young duke touches perishes, his existence is far from lonely. His spirited siblings Viola and Walter, loyal butler Rob, and the eccentric witches Cuff and Zain are regularly livening up his secluded mansion. But it is the duke's flirty maid, Alice Lendrott, who fills him with the promise of a beautiful tomorrow and the determination to break this hellish spell cast upon him. The identity of the witch who cursed the duke has been uncovered thanks to Zain's unique magic and the help of Daleth, the leader of the underworld. However, the villain is a notoriously fearsome and powerful witch, who, enraged by the duke and his companions' trip back in time, torments them even from the afterlife. Moreover, an unexpected loved one reappears, strengthening the duke and Alice's hope to break the curse and finally close the distance between them.")
        }
    }

    @Test
    fun `returns NoRawSynopsis`() {
        runBlocking {
            // given
            val testRawDataRetriever = object: RawDataRetriever by TestRawDataRetriever {
                override suspend fun retrieveRawData(id: AnimeId): String = loadTestResource("synopsis/notify/no-synopsis.json")
            }

            val scoreLoader = NotifyRawSynopsisLoader(
                appConfig = TestConfig,
                rawDataRetriever = testRawDataRetriever,
            )

            // when
            val result = scoreLoader.loadRawSynopsis(URI("https://${NotifyConfig.hostname()}/anime/1535"))

            // then
            assertThat(result).isEqualTo(NoRawSynopsis)
        }
    }
}