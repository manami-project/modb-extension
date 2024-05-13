package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

internal class RawSynopsisTest {

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `throws exception of test is blank`(input: String) {
        // when
        val result = exceptionExpected<IllegalArgumentException> {
            RawSynopsis(input)
        }

        // then
        assertThat(result).hasMessage("Text must not be blank.")
    }

    @Test
    fun `correctly returns wordCount`() {
        // given
        val testSynopsis = RawSynopsis("Travelling merchant Kraft Lawrence earns his living by selling his wares at good prices in various villages, one of which is Pasloe. Once blessed by the wise harvest goddess Holo, it is becoming more and more independent. As the cornfields gradually produced more crops, little by little, the wise goddess became a myth and, nowadays, only appears in the village’s legends. One day, when Lawrence passes through Pasloe on his journey, a harvest festival is held in honour of Holo – though it is more tradition than superstition that drives the villagers. This thought soon turns out to be a misconception: that night, Lawrence finds a girl with wolf ears and a tail in his wagon, who quickly turns out to be the wolf goddess Holo herself. She wishes to return to her old hometown in the north – and Lawrence is supposed to help her! Lawrence, on the other hand, could also do with her help as he wants to open his own shop, and her skills could come in handy. So, the adventure of the unlikely duo takes its course …")

        // when
        val result = testSynopsis.wordCount()

        // then
        assertThat(result).isEqualTo(183)
    }
}