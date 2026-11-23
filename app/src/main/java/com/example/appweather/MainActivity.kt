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

    var bgThreadName by remember { mutableStateOf("запроса на  httpbin.org не создан") }


    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {

        Text(text =  bgThreadName)


        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {


                val myThread = Thread {
                    try {//строхуемся от от ошибок при запросе
                        /* Стандартный URL(...).readText() умеет делать только простой GET-запрос  */
                        val response = fetchGet()

                        bgThreadName = "вот ответ с httpbin:\n$response" //передал запрос в переменную состояния

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

fun fetchGet(): String = URL("https://httpbin.org/get").readText()
/*
                                Создаем фоновый поток в ручную
       но в него вставляем фу fetchGet()которая просто запрашивает данные с сервера
       в переменную записываю название запрос не создан и ее вставляю в текст
       с присвоением результата фу в эту переменную записываю  вот ответ присылает
       чистый json

*/