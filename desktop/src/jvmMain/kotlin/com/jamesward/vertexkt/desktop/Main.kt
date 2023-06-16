package com.jamesward.vertexkt.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.jamesward.vertexkt.core.Connection
import com.jamesward.vertexkt.core.Instance
import com.jamesward.vertexkt.core.Message
import com.jamesward.vertexkt.core.Parameters
import com.jamesward.vertexkt.core.PredictRequest
import com.jamesward.vertexkt.core.predict
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatUI(connection: Connection) {
    val parameters = Parameters(0.3, 200, 0.8, 40)
    val predictions = remember { mutableStateOf(PredictRequest(emptyList(), parameters)) }
    val userMessage = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    suspend fun sendMessage() {
        val instances = predictions.value.instances.firstOrNull() ?: Instance("", emptyList(), emptyList())
        val messages = instances.messages + Message("user", userMessage.value)
        predictions.value = PredictRequest(listOf(Instance("", emptyList(), messages)), predictions.value.parameters)

        val predictResponse = connection.predict("chat-bison", predictions.value)

        val updatedMessages = messages + predictResponse.predictions.first().candidates
        predictions.value = PredictRequest(listOf(Instance("", emptyList(), updatedMessages)), predictions.value.parameters)

        userMessage.value = ""
    }

    Column {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(predictions.value.instances.firstOrNull()?.messages ?: emptyList()) { message ->
                val textPrefix = if (message.author == "user") {
                    "> "
                } else {
                    "< "
                }
                Text(text = textPrefix + message.content)
            }
        }

        Row {
            OutlinedTextField(
                userMessage.value,
                { userMessage.value = it },
                singleLine = true,
                modifier = Modifier.onKeyEvent {
                    if (it.key == Key.Enter) {
                        scope.launch {
                            sendMessage()
                        }
                    }
                    false
                }
            )

            Button({
                scope.launch {
                    sendMessage()
                }
            }, modifier = Modifier.padding(start = 10.dp)) {
                Text("Send")
            }
        }
    }
}

fun main() {
    val accessToken = System.getenv("GCLOUD_ACCESS_TOKEN")
    if (accessToken == null) {
        throw Exception("Set the GCLOUD_ACCESS_TOKEN env var")
    }

    val projectId = System.getenv("GCLOUD_PROJECT")
    if (projectId == null) {
        throw Exception("Set the GCLOUD_PROJECT env var")
    }

    Connection(accessToken, projectId, "us-central1").use { connection ->
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "VertexKt",
            ) {
                MaterialTheme {
                    ChatUI(connection)
                }
            }
        }
    }
}