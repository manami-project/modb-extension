package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.httpclient.RequestBody
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import java.net.URI
import java.net.URL

internal object TestHttpClient : HttpClient {
    override suspend fun post(url: URL, requestBody: RequestBody, headers: Map<String, Collection<String>>): HttpResponse = shouldNotBeInvoked()
    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = shouldNotBeInvoked()
}

internal object TestFileAccessor : FileAccessor {
    override suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue = shouldNotBeInvoked()
    override suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData) = shouldNotBeInvoked()
}