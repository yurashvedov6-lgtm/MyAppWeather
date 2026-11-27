package com.example.appweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    var weatherText by remember { mutableStateOf("Выберите город из списка ниже:") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {

        // Блок с результатом погоды
        Text(text = weatherText)

        Spacer(modifier = Modifier.height(16.dp))

        // Прокручиваемый список кнопок для каждого города из CrimeaCities.kt
        LazyColumn {
            items(crimeaCities) { city ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = {
                        scope.launch {
                            weatherText = "Загружаем погоду: ${city.name}..."
                            try {
                                // Передаём выбранный объект city в fetchGet
                                val rawJson = withContext(Dispatchers.IO) { fetchGet(city) }

                                val jsonObject = JSONObject(rawJson)
                                val current = jsonObject.getJSONObject("current")

                                val temp = current.getDouble("temperature_2m")
                                val windSpeed = current.getDouble("wind_speed_10m")
                                val precipitation = current.getDouble("precipitation")
                                val time = current.getString("time")

                                weatherText = "Погода (${city.name}):\n" +
                                        "Температура: $temp °C\n" +
                                        "Скорость ветра: $windSpeed км/ч\n" +
                                        "Осадки: $precipitation мм\n" +
                                        "Время обновления (UTC): $time"

                            } catch (e: Exception) {
                                weatherText = "Ошибка: ${e.javaClass.simpleName} — ${e.message}"
                            }
                        }
                    }
                ) {
                    Text(text = city.name)
                }
            }
        }
    }
}

// Теперь функция принимает объект City и сама подставляет его координаты
fun fetchGet(city: City): String {
    // Добавьте "api." перед open-meteo:
    val host = "https://" + "api." + "open-meteo.com"
    val path = "/v1/forecast"
    val params = "?latitude=${city.latitude}&longitude=${city.longitude}&current=temperature_2m,wind_speed_10m,precipitation"

    val apiUrl = host + path + params

    val connection = URL(apiUrl).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    return connection.inputStream.bufferedReader().readText()
}

/*
в том файле я подключаю класс city из второго созданного файла
*/