package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.test.shouldNotBeInvoked
import java.net.URI

internal object TestRawScoreLoader: RawScoreLoader {
    override suspend fun loadRawScore(source: URI): RawScoreReturnValue = shouldNotBeInvoked()
}