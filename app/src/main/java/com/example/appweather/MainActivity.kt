package com.example.appweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleMainThreadBrick()
        }
    }
}

@Composable
fun SimpleMainThreadBrick() {
    var resultText by remember { mutableStateOf("Выберите действие: POST или GET") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {

        Text(text = resultText)

        Spacer(modifier = Modifier.height(16.dp))

        // КНОПКА 1: Отправляем данные на сервер (POST) и парсим ответ
        Button(
            onClick = {
                scope.launch {
                    try {
                        val jsonToSend = """{"city": "Simferopol", "temp": 18}"""
                        //уводим фу fetchPost в сетевой поток Dispatchers.IO
                        val rawJson = withContext(Dispatchers.IO) { fetchPost(jsonToSend) }

                        // Парсим ответ сервера (он вернёт наши данные + присвоенный id)
                        val jsonObject = JSONObject(rawJson)
                        val id = jsonObject.getInt("id")
                        val city = jsonObject.getString("city")
                        val temp = jsonObject.getInt("temp")

                        resultText = "POST успешен! Сервер принял:\n" +
                                "ID записи: $id\n" +
                                "Город: $city\n" +
                                "Температура: $temp°C"
                    } catch (e: Exception) {
                        resultText = "Ошибка POST: ${e.javaClass.simpleName} — ${e.message}"
                    }
                }
            }
        ) {
            Text("1. Отправить данные (POST)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // КНОПКА 2: Забираем готовые данные с этого же сервера (GET)
        Button(
            onClick = {
                scope.launch {
                    try {
                        val rawJson = withContext(Dispatchers.IO) { fetchGet() }

                        // Парсим полученную с сервера запись №1
                        val jsonObject = JSONObject(rawJson)
                        val userId = jsonObject.getInt("userId")
                        val title = jsonObject.getString("title")

                        resultText = "GET успешен! Получено с сервера:\n" +
                                "User ID: $userId\n" +
                                "Заголовок: $title"
                    } catch (e: Exception) {
                        resultText = "Ошибка GET: ${e.javaClass.simpleName} — ${e.message}"
                    }
                }
            }
        ) {
            Text("2. Получить данные (GET)")
        }
    }
}

fun fetchPost(jsonBody: String): String {
    val connection = URL("https://jsonplaceholder.typicode.com/posts").openConnection() as HttpURLConnection
    connection.apply {
        requestMethod = "POST"
        doOutput = true
        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
    }
    connection.outputStream.use { os ->
        os.write(jsonBody.toByteArray(Charsets.UTF_8))
    }
    return connection.inputStream.bufferedReader().readText()
}

fun fetchGet(): String {
    val connection = URL("https://jsonplaceholder.typicode.com/posts/1").openConnection() as HttpURLConnection
    connection.requestMethod = "GET"// обрабатываем запрос и отправляем
    return connection.inputStream.bufferedReader().readText()// получаем ответ
}
/*
                     Сдесь тот же код что и два перед этим кода тоесть один запрос POST
       Другой GET также сделали через клас URL() и две фу fetcPost() и fetchGet()
       перед этим коммитом два одинаковых таких только по отдельности а сдесь сделали две кнопки с
       двумя одну переменную для корутины scope через которую запускали в двух кнопках (button)через
    метод scopeLaunch{}   И МЕТОД СЕТЕВОЙ ПОТОК withContext(Despedcher.IO)  тоесть фоновый и в нем
    запускаем запросы к серверу парсим их тоесь пишем поля который нам нужны в запросе  это сервер
    typecode.come он отправляет сразу ответ post какие данные я отправил а get запрос только
    выдает id этого запроса это учебный сервер
*/