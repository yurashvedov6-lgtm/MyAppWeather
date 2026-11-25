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

                        //1.Получаем сырую строку с вервера в фоновом потоке
                        val rowJson = withContext(Dispatchers.IO) { fetchGet() }
                //2.ПАРСИНГ:превращаем сырую строку в JSONObject и достаем нужные поля
                        val jsonObject = JSONObject(rowJson)
                        val  myIp =jsonObject.getString("origin")//достаем ip
                        val  requestUrl = jsonObject.getString("url")// достаем url
                        //достаем из вложнного поля "headers" ->"Host"
                        val headersObject = jsonObject.getJSONObject("headers")
                        val host =headersObject.getString("Host")

                        //отправка в Text
                        bgThreadName = "Результат парсинга:\n" +
                                "Ваш IP (origin): $myIp\n" +
                                "Адрес (url): $requestUrl\n" +
                                "Хост (из headers): $host"

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

fun fetchGet(): String {
    val connection = URL("https://httpbin.org/get ").openConnection() as HttpURLConnection
    /*connection.apply {
        requestMethod = "POST"
        doOutput = true //1.разрешаем отправку тела запроса
        // 2. Говорим серверу, что шлём JSON
        setRequestProperty("Content-Type","application/json; charset = UTF-8")
    }
    */
    /*
    //3.Записываем данные в поток отправки
    connection.outputStream.use { os ->
        os.write(jsonBody.toByteArray(Charsets.UTF_8))
    }
    */
    connection.requestMethod = "GET"
    // 4. Читаем ответ сервера (httpbin вернёт ваши данные в поле "json" и "data")
    return connection.inputStream.bufferedReader().readText()
}
/*
            Теперь в этом коде мы делаем запрос GET но не чистый json фармат а делаем парсинг.

           ЗАКОМЕНТИРОВАЛ СТРОКИ В МЕТОДЕ POST
        /============================================================================/
        Чтобы получить данные method GET  запросе через стандартный HttpURLConnection И затем
        сделать ПАРСИНГ ДЛЯ ЭТОГО ЕСТЬ СПЕЦИАЛЬНЫЙ КЛАСС ВСТРОЕННЫЙ В АНДРОИД JSONObject берем
        строку из фонового потока создаем объект в виде переменной и делаем обект уже этого класа
        JSONObject   с помощью такой команды  val jsonObject = JSONObject(rowJson) и к этому обекту
        приципляем свойства класса через точку и выводим поля которые нам нужны из json формата
*/