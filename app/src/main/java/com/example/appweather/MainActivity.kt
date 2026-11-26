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
            WeatherScreen()
        }
    }
}

@Composable
fun WeatherScreen() {
    var weatherText by remember { mutableStateOf("Нажмите кнопку, чтобы узнать погоду в Симферополе") }
    val scope = rememberCoroutineScope() // 1. Создаём менеджер корутин для экрана

    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {

        Text(text = weatherText)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // 2. Запускаем корутину
                scope.launch {
                    weatherText = "Загружаем погоду..."
                    try {
                        // 3. Делаем GET-запрос в фоновом потоке Dispatchers.IO
                        val rawJson = withContext(Dispatchers.IO) { fetchGet() }

                        // 4. ПАРСИНГ ответа Open-Meteo
                        val jsonObject = JSONObject(rawJson)//забираем пришедший json

                        // Открываем вложенный объект "current_weather"
                        val currentWeather = jsonObject.getJSONObject("current_weather")

                        // Достаём конкретные поля (числа через getDouble, строки через getString)
                        val temp = currentWeather.getDouble("temperature")
                        val windSpeed = currentWeather.getDouble("windspeed")
                        val time = currentWeather.getString("time")

                        weatherText = "Погода в Симферополе:\n" +
                                "Температура: $temp °C\n" +
                                "Скорость ветра: $windSpeed км/ч\n" +
                                "Время обновления (UTC): $time"

                    } catch (e: Exception) {
                        weatherText = "Ошибка: ${e.javaClass.simpleName} — ${e.message}"
                    }
                }
            }
        ) {
            Text("Узнать погоду")
        }
    }
}

fun fetchGet(): String {
    val host = "https://api.open-meteo.com"
    val path = "/v1/forecast"
    val params = "?latitude=44.9572&longitude=34.1108&current_weather=true"

    val apiUrl = host + path + params

    val connection = URL(apiUrl).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    return connection.inputStream.bufferedReader().readText()
}

/*
сдесь код тот же самый как и в предыдущих get только запрос GET
 делается уже на сервер Open-Meteo
 */