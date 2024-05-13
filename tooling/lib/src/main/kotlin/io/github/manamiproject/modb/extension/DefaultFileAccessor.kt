package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.core.json.Json.SerializationOptions.DEACTIVATE_SERIALIZE_NULL
import kotlinx.coroutines.withContext
import java.net.URI

/**
 * @since 1.0.0
 * @property httpCient
 */
internal class DefaultFileAccessor(
    private val httpCient: HttpClient = DefaultHttpClient(),
) : FileAccessor {

    override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue = withContext(LIMITED_CPU) {
        require(sources.isNotEmpty()) { "Sources must not be empty." }
        val filename = filename(sources)

        val jsonString = when (origin) {
            is LocalFileOrigin -> {
                val file = origin.resolve(filename)
                if (file.regularFileExists()) {
                    file.readFile().ifBlank { return@withContext ExtensionDataNotFound }
                } else {
                    return@withContext ExtensionDataNotFound
                }
            }
            is UriOrigin, is ModbExtensionRepoOrigin -> {
                val url = (origin.resolve(filename) as URI).toURL()
                val response = httpCient.get(url)
                if (response.isOk() && response.bodyAsText.isNotBlank()) {
                    response.bodyAsText
                } else {
                    return@withContext ExtensionDataNotFound
                }
            }
        }

        return@withContext Json.parseJson<ExtensionData>(jsonString)!!
    }

    override suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData) = withContext(LIMITED_FS) {
        val filename = filename(extensionData.sources)
        val file = directory.resolve(filename)
        Json.toJson(extensionData, DEACTIVATE_SERIALIZE_NULL).writeToFile(file)
    }
}