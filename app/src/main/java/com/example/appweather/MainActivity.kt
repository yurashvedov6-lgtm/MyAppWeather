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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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


    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {

        Text(text =  bgThreadName)


        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {


                val myThread = Thread {
                    try {//строхуемся от от ошибок при запросе
                        /* Стандартный URL(...).readText() умеет делать только простой GET-запрос  */
                        val response = fetchPut()

                        bgThreadName = "отправка PUT и возвр с сервера  httpbin.org:\n$response" //передал запрос в переменную состояния

                    } catch (e: Exception) {
                        //показывает если при запрое пошло чтото не так и передает в перем сост
                        bgThreadName = "Ошибка: ${e.javaClass.simpleName} — ${e.message}"
                    }


                }
                myThread.start()
            }
        ) {
            Text("Запрос url")
        }

    }
}

/*  1. .openConnection() — метод класса URL,
который подготавливает сетевой канал к серверу.
Проблема в том, что по умолчанию он возвращает слишком общий,
абстрактный родительский класс URLConnection
 (он подходит и для локальных файлов, и для FTP,
 поэтому у него ещё нет свойства requestMethod).
 /==================================================/
  2. as HttpURLConnection — это приведение типа (type casting) в Kotlin.
  С помощью слова as вы говорите компилятору:
  «Я точно знаю, что этот адрес начинается с https://,
  поэтому рассматривай это общее соединение именно
   как специализированное HTTP-соединение (HttpURLConnection)».*/

fun fetchPut(): String = (URL("https://httpbin.org/put").openConnection() as HttpURLConnection)
    /*1. .apply — это встроенная функция области видимости (scope function) в Kotlin.
      Её главная задача: «возьми объект,
    выполни над ним блок настроек и верни сам этот объект дальше».
   /================================================================/
   2. { requestMethod = "POST" } — внутри фигурных скобок контекстом (this)
   становится сам объект HttpURLConnection.
   Поэтому вам не нужно снова писать имя переменной
   (например, conn.requestMethod = "POST"),
   вы сразу обращаетесь к его свойству requestMethod и ставите значение "POST".
     */
    .apply { requestMethod = "PUT" }
    /*1. .inputStream — труба с сырыми байтами.
По сети данные приходят не в виде букв, а как непрерывный поток нулей и единиц (байтов).
inputStream — это «шланг», подключённый к серверу, из которого начинают течь эти сырые байты.
В этот же момент происходит сама отправка запроса на сервер и ожидание его ответа.
2. .bufferedReader() — переводчик с буфером (накопителем).
	• Во-первых, он переводит сырые байты в понятные человеку символы и буквы (по умолчанию в кодировке UTF-8).
	• Во-вторых, слово Buffered означает, что он не дёргает сеть ради каждого отдельного байта,
	 а собирает данные крупными порциями в память (буфер), чтобы чтение работало быстро и не тормозило телефон.
3. .readText() — финальный сборщик строки.
Это удобная функция-расширение в Kotlin: она вычитывает все символы из bufferedReader до самого конца ответа,
склеивает их в единую строку String и автоматически закрывает поток за собой, чтобы не утекала память.
*/
    .inputStream.bufferedReader().readText()

/*
                                Создаем фоновый поток в ручную
и изучаем метод post он на много сложнее чем метод get

*/