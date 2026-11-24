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

    var bgThreadName by remember { mutableStateOf("отправка  put  httpbin.org ") }
    val scope = rememberCoroutineScope()// 1.Создаем мененджен корутин для этого экрана

    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {

        Text(text = bgThreadName)


        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {


                // 2.запускаем легкую корутину вместо тяжелого Thread()
                scope.launch {
                    try {//строхуемся от от ошибок при запросе
                        //3.Уводим фу fetchPut  в сетевой поток Dispedchers.IO
                        // Данные, которые отправляем на сервер (в формате JSON)
                        val jsonData = """{"city": "Moscow", "temp": 22}"""
                        val response = withContext(Dispatchers.IO) { fetchPOST(jsonData) }

                        bgThreadName = "отправка POST и возвр с сервера  httpbin.org:\n$response"

                    } catch (e: Exception) {
                        //показывает если при запрое пошло чтото не так и передает в перем сост
                        bgThreadName = "Ошибка: ${e.javaClass.simpleName} — ${e.message}"
                    }


                }
            }
        ) {
            Text("Запрос url")
        }

    }
}


//fun fetchPut(): String = (URL("https://httpbin.org/post").openConnection() as HttpURLConnection)


    //.apply { requestMethod = "POST" }//обрабатываем пустой post connection

    //.inputStream.bufferedReader().readText()//читаем ответ сервера

fun fetchPOST(jsonBody: String): String{
    val connection = URL("https://httpbin.org/post").openConnection() as HttpURLConnection
    connection.apply {
        requestMethod = "POST"
        doOutput = true //1.разрешаем отправку тела запроса
        // 2. Говорим серверу, что шлём JSON
        setRequestProperty("Content-Type","application/json; charset = UTF-8")
    }
    //3.Записываем данные в поток отправки
    connection.outputStream.use { os ->
        os.write(jsonBody.toByteArray(Charsets.UTF_8))
    }
    // 4. Читаем ответ сервера (httpbin вернёт ваши данные в поле "json" и "data")
    return connection.inputStream.bufferedReader().readText()
}
/*
            Теперь в этом коде мы пытаемся сделате не пустой request POST а его заполнить.

я закоментировал две строчки оно тоже самое будет но чучуть по другому что мы добавляем в заполнен
ном POST
        /============================================================================/
        Чтобы отправить данные в POST-запросе через стандартный HttpURLConnection, нужно:
1. Изменить адрес на https://httpbin.org и метод на "POST".
2. Включить передачу данных (doOutput = true).
3. Указать заголовок формата данных (обычно JSON: Content-Type: application/json).
4. Записать строку с данными в outputStream перед чтением ответа.
*/