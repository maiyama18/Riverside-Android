package com.example.riverside.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import javax.inject.Inject

class FeedApiException(message: String) : Exception(message)
class FeedInvalidResponseException(message: String) : Exception(message)

class FeedFetcher @Inject constructor(
    private val httpClient: HttpClient,
) {
    private val endpointUrl = "https://rssproxy-6q4koorr7a-an.a.run.app/riverside/feeds"

    suspend fun fetchFeed(url: String, force: Boolean): FeedResponse {
        val response = request(listOf(url), force)
        val feedResult = response.feeds[url]
            ?: throw FeedInvalidResponseException("response for $url not found")
        if (feedResult.error != null) {
            throw FeedApiException(feedResult.error)
        } else {
            return feedResult.feed
                ?: throw FeedInvalidResponseException("response for $url is invalid")
        }
    }

    private suspend fun request(urls: List<String>, force: Boolean): FeedsResponse =
        httpClient.get(endpointUrl) {
            url {
                parameters.append("urls", urls.joinToString(","))
                parameters.append("force", force.toString())
            }

            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
        }.body()
}