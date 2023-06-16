package com.jamesward.vertexkt.core

import kotlinx.coroutines.runBlocking
import kotlin.test.*

class PredictTest {

    @Test
    fun predict(): Unit = runBlocking {
        val accessToken = System.getenv("GCLOUD_ACCESS_TOKEN")
        if (accessToken == null) {
            fail("Set the GCLOUD_ACCESS_TOKEN env var")
        }

        val projectId = System.getenv("GCLOUD_PROJECT")
        if (projectId == null) {
            fail("Set the GCLOUD_PROJECT env var")
        }

        Connection(accessToken, projectId, "us-central1").use { connection ->
            val response = connection.predict("", listOf("say hello, world"), "chat-bison")
            assertEquals(response.predictions.first().candidates.first().content, "Hello, world!")
        }
    }

}
