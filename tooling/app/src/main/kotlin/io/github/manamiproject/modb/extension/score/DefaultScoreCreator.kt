package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.anidb.AnidbConfig
import io.github.manamiproject.modb.anilist.AnilistConfig
import io.github.manamiproject.modb.animeplanet.AnimePlanetConfig
import io.github.manamiproject.modb.anisearch.AnisearchConfig
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.kitsu.KitsuConfig
import io.github.manamiproject.modb.livechart.LivechartConfig
import io.github.manamiproject.modb.myanimelist.MyanimelistConfig
import io.github.manamiproject.modb.notify.NotifyConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.URI
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * @since 1.0.0
 * @property rawScoreLoader
 */
class DefaultScoreCreator(
    private val appConfig: Config,
    private val rawScoreLoader: Map<Hostname, RawScoreLoader> = mapOf(
        AnidbConfig.hostname() to AnidbRawScoreLoader(appConfig),
        AnilistConfig.hostname() to AnilistRawScoreLoader(appConfig),
        AnimePlanetConfig.hostname() to AnimePlanetRawScoreLoader(appConfig),
        AnisearchConfig.hostname() to AnisearchRawScoreLoader(appConfig),
        KitsuConfig.hostname() to KitsuRawScoreLoader(appConfig),
        LivechartConfig.hostname() to LivechartRawScoreLoader(appConfig),
        MyanimelistConfig.hostname() to MyanimelistRawScoreLoader(appConfig),
        NotifyConfig.hostname() to NotifyRawScoreLoader(appConfig),
    )
): ScoreCreator {

    override suspend fun createScore(sources: Collection<URI>): ScoreReturnValue = withContext(LIMITED_NETWORK) {
        val jobs = mutableListOf<Deferred<RawScoreReturnValue>>()

        sources.forEach {
            jobs.add(async { rawScoreLoader[it.host]!!.loadRawScore(it) })
        }

        val rawScores = jobs.awaitAll()
            .filterNot { it is NoRawScore }
            .map { it as RawScore }
            .map { it.scaledValue() }

        if (rawScores.isEmpty()) {
            return@withContext Score()
        }

        return@withContext Score(
            arithmeticMean = arithmeticMean(rawScores),
            arithmeticGeometricMean = arithmeticGeometricMean(rawScores),
            median = median(rawScores),
        )
    }

    private fun arithmeticMean(values: List<Double>) = values.sum() / values.size.toDouble()

    private fun median(values: List<Double>): Double {
        return if (values.size % 2 == 0) {
            val sorted = values.sorted()
            val lower = sorted.subList(0, values.size/2).last()
            val upper = sorted.subList(values.size/2, values.size).first()
            (lower + upper) / 2.0
        } else {
            val middle = ( values.size.takeIf { it == 1 } ?: round(values.size.toDouble() / 2.0).toInt() ) - 1
            values.sorted()[middle]
        }
    }

    private fun arithmeticGeometricMean(values: List<Double>, epsilon: Double = 1.0E-256): Double {
        var am = arithmeticMean(values)
        var gm = values.reduce { acc, d -> acc * d }.pow( 1.0 / values.size.toDouble())

        var previousAm = 0.0
        var previousGm = 0.0

        while (am != gm && difference(am, gm) > epsilon && (am != previousAm && gm != previousGm)) {
            previousAm = am
            am = arithmeticMean(listOf(am, gm))
            previousGm = gm
            gm = sqrt(previousAm * gm)
        }

        return gm
    }

    private fun difference(number1: Double, number2: Double): Double {
        val message = "Passed numbers must be positive or 0"
        require(number1 >= 0) { message }
        require(number2 >= 0) { message }

        val higher = if (number1 > number2) number1 else number2
        val lower = if (number1 < number2) number1 else number2

        return higher - lower
    }
}