package com.jamesward.vertexkt.core

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

suspend fun Connection.predict(model: String, predictRequest: PredictRequest): PredictResponse {
    val url = Url("https://$region-aiplatform.googleapis.com/v1/projects/$projectId/locations/$region/publishers/google/models/$model:predict")

    val response = client.post(url) {
        header(HttpHeaders.Authorization, "Bearer $accessToken")
        contentType(ContentType.Application.Json)
        setBody(predictRequest)
    }

    if (response.status.isSuccess()) {
        return response.body<PredictResponse>()
    }
    else {
        throw Exception(response.bodyAsText())
    }
}

suspend fun Connection.predict(context: String, messages: List<String>, model: String): PredictResponse {
    val predictRequest = PredictRequest(
        listOf(
            Instance(
                context,
                emptyList(),
                messages.map { s ->
                    Message("user", s)
                }
            )
        ),
        Parameters(0.3, 200, 0.8, 40)
    )
    return predict(model, predictRequest)
}

@OptIn(ExperimentalStdlibApi::class)
data class Connection(val accessToken: String, val projectId: String, val region: String): AutoCloseable {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override fun close() {
        client.close()
    }
}


@Serializable
data class Content(val content: String)

@Serializable
data class Example(val input: Content, val output: Content)

@Serializable
data class Message(val author: String, val content: String)

@Serializable
data class Instance(val context: String, val examples: List<Example>, val messages: List<Message>)

@Serializable
data class Parameters(val temperature: Double, val maxDecodeSteps: Int, val topP: Double, val topK: Int)

@Serializable
data class PredictRequest(val instances: List<Instance>, val parameters: Parameters)

@Serializable
data class Prediction(val candidates: List<Message>)

@Serializable
data class PredictResponse(val predictions: List<Prediction>)
