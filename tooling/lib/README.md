# modb-extension lib

This lib allows users to access the data conveniently and is also used by the app to persist the data.

## Usage

Here is an example on how to use the lib.
Each property has its own package, and `*Reader` implementations.
Lets assume that you want to access a score and set up an example based on that.
However, this will work for any other anime data accordingly. The concepts and structure is always the same.

1. Instantiate a `ScoreReader` by using `DefaultScoreReader`
2. `DefaultScoreReader` requires an `Origin`. An `Origin` is a class which tells the implementation where exactly to retrieve the data. The easiest way is to use `ModbExtensionRepoOrigin` which downloads the data directly from the github repo.
3. Handle the return value. The `findScore()` function retuns a `ScoreReturnValue` which is a sealed class having two possible implementations: `Score` the class which actually contains the data and `ScoreNoteFound` which indicates that no data was found.

Here is a simplified exmaple:

```kotlin
import io.github.manamiproject.modb.extension.score.DefaultScoreReader
import io.github.manamiproject.modb.extension.score.Score
import io.github.manamiproject.modb.extension.score.ScoreNoteFound
import io.github.manamiproject.modb.extension.score.ScoreReader

suspend fun main() {
    val anime: Anime
    val scoreReader: ScoreReader = DefaultScoreReader(origin = ModbExtensionRepoOrigin)
    
    when (val scoreResult = scoreReader.findScore(anime)) {
        is Score -> TODO()
        is ScoreNoteFound -> TODO()
    }
}
```

Here is another example requesting both score and synopsis by omitting the check of the return value.
Instead of an `Anime` object you can use a `Collection<URI>`. But keep in mind that the value has to exactly match `sources` property of an anime in [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database)

```kotlin
import io.github.manamiproject.modb.extension.ModbExtensionRepoOrigin
import io.github.manamiproject.modb.extension.score.DefaultScoreReader
import io.github.manamiproject.modb.extension.score.Score
import io.github.manamiproject.modb.extension.synopsis.DefaultSynopsisReader
import io.github.manamiproject.modb.extension.synopsis.Synopsis
import io.github.manamiproject.modb.serde.json.AnimeListJsonStringDeserializer
import io.github.manamiproject.modb.serde.json.DefaultExternalResourceJsonDeserializer
import io.github.manamiproject.modb.serde.json.models.Dataset
import java.net.URI

suspend fun main() {
    val deserializer = DefaultExternalResourceJsonDeserializer(deserializer = AnimeListJsonStringDeserializer())
    val allAnime: Dataset = deserializer.deserialize(URI("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database.zip").toURL())
    val deathNote = allAnime.data.first { it.title == "Death Note" }

    val score  = DefaultScoreReader(ModbExtensionRepoOrigin).findScore(deathNote) as Score
    val synopsis  = DefaultSynopsisReader(ModbExtensionRepoOrigin).findSynopsis(deathNote) as Synopsis

    println("""
        ${deathNote.title}
        ${"-".repeat(deathNote.title.length)}        
        arithmetic mean:           ${score.arithmeticMean}
        arithmetic geometric mean: ${score.arithmeticGeometricMean}
        median:                    ${score.median}
        
        ${synopsis.text}
    """.trimIndent())
}
```