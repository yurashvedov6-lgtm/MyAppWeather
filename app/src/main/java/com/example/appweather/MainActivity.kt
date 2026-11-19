package com.example.appweather

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWeather(name = "Simferopol")
        }
    }
}

@Composable
fun MyWeather(name: String) {
    val state = remember { mutableStateOf("unknow") }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize() // Растягивает колонку на 100% ширины и высоты экрана
    ) {
        Box(
            contentAlignment = Alignment.Center, // Располагает текст по центру блока
            modifier = Modifier
                .fillMaxWidth() // Занимает всю ширину экрана
                .fillMaxHeight(0.5f) // Занимает верхнюю половину высоты (50%)
                .background(color = Color.Blue)
        ) {
            Text(
                text = "$name = ${state.value}",
                fontSize = 20.sp,
                modifier = Modifier
                    .background(color = Color.Gray)
            )
        }
        Box(
            contentAlignment = Alignment.BottomCenter, // Располагает кнопку по центру внизу
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color.Yellow)
                .padding(bottom = 20.dp)
        ) {
            Button(
                onClick = {
                    getResalt(city = "Simferopol", state = state, context = context)
                },
                modifier = Modifier
                    .fillMaxWidth() // Кнопка во всю ширину
            ) {
                Text(
                    text = "push me "
                )
            }
        }
    }
}

// Функция для отправки сетевого запроса к API Open-Meteo и обновления экрана
private fun getResalt(city: String, state: MutableState<String>, context: Context) {
    // 1. СБОРКА АДРЕСА ЗАПРОСА (URL)
    // Разбиваем ссылку на части для читаемости: сервер, путь к разделу прогноза и параметры координат
    val host = "api.open-meteo.com"

    val path = "/v1/forecast"
    // latitude — широта, longitude — долгота (Симферополь), current_weather=true — запрос текущей погоды
    val query = "?latitude=44.9572&longitude=34.1108&current_weather=true"
    val url = "https://$host$path$query"

    // 2. СОЗДАНИЕ ОЧЕРЕДИ ЗАПРОСОВ VOLLEY
    // Volley управляет сетевыми потоками в фоне, чтобы интерфейс не «зависал» при ожидании ответа
    val queue = Volley.newRequestQueue(context)

    // 3. ФОРМИРОВАНИЕ ЗАПРОСА НА ПОЛУЧЕНИЕ ТЕКСТА (StringRequest)
    val request = StringRequest(
        Request.Method.GET, // Тип HTTP-запроса: GET используется для получения данных с сервера
        url,                // Собранный выше полный адрес API
        { response ->
            // 4. БЛОК УСПЕШНОГО ОТВЕТА (срабатывает, когда сервер прислал данные)
            // Превращаем сырую строку ответа (response) в удобный для поиска JSON-объект
            val jsonObject = JSONObject(response)

            // Достаем вложенный блок "current_weather", внутри которого лежат показатели погоды
            val currentWeather = jsonObject.getJSONObject("current_weather")

            // Извлекаем конкретные числа по их ключам (именам полей в ответе сервера)
            val temp = currentWeather.getDouble("temperature")
            val wind = currentWeather.getDouble("windspeed")

            // 5. ОБНОВЛЕНИЕ СОСТОЯНИЯ ЭКРАНА (РЕКОМПОЗИЦИЯ)
            // Записываем готовую строку в state.value — Compose сам заметит изменение и перерисует Text
            state.value = "Температура: $temp°C, ветер: $wind км/ч"
        },
        { error ->
            // БЛОК ОШИБКИ (срабатывает, если нет интернета, неверный URL или сервер недоступен)
            state.value = "Ошибка загрузки: ${error.message}"
        }
    )

    // 6. ОТПРАВКА ЗАПРОСА В СЕТЕВУЮ ОЧЕРЕДЬ
    // Без этой строки запрос останется просто подготовленным объектом и никуда не уйдет
    queue.add(request)
}

/*
================================================================================
ШПАРГАЛКА: КАК РАБОТАЕТ ПРИЛОЖЕНИЕ И СЕТЕВОЙ ЗАПРОС К API
================================================================================

1. Инициализация при запуске (данные идут сверху вниз):
   • MainActivity запускает экран MyWeather(name = "Simferopol").
   • remember { mutableStateOf("unknow") } создает реактивную ячейку памяти (state).
   • Компонент Text читает name и state.value и отрисовывает начальный текст на экране.

2. Действие пользователя (событие идет снизу вверх):
   • Нажатие на Button запускает функцию getResalt(city, state, context).
   • В функцию передается ссылка на ячейку state (чтобы изменить её позже) и context (для работы сети).

3. Работа с API через Volley (асинхронно в фоне):
   • Формируется HTTP GET-запрос с координатами города.
   • queue.add(request) ставит запрос в фоновую очередь — экран не блокируется.
   • Сервер Open-Meteo возвращает ответ в формате JSON (структура «ключ: значение»).
   • JSONObject разбирает текст, находит объект "current_weather" и вытаскивает "temperature" и "windspeed".

4. Автоматическое обновление экрана (Рекомпозиция):
   • Когда getResalt меняет state.value, Jetpack Compose автоматически перерисовывает только Text.

ЧТО ИЗУЧИТЬ ДАЛЬШЕ ДЛЯ УВЕРЕННОЙ РАБОТЫ С API:
1. HTTP и REST API: методы GET/POST, query-параметры (?key=value&key2=value2), коды ответов (200, 404, 500).
2. Формат JSON: разница между объектами {} (JSONObject) и списками/массивами [] (JSONArray).
3. Корутины Kotlin (Coroutines): современный способ работы с фоновыми задачами вместо колбэков.
4. Retrofit / Ktor + Gson / Kotlinx Serialization: современные библиотеки, которые превращают JSON сразу в data-классы.
5. Архитектура MVVM (ViewModel): вынос сетевых запросов из UI-кода, чтобы данные не терялись при повороте экрана.
================================================================================
*/
