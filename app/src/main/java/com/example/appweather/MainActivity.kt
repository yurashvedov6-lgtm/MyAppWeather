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
                        val response = withContext(Dispatchers.IO) { fetchPut() }

                        bgThreadName = "отправка PUT и возвр с сервера  httpbin.org:\n$response"

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


fun fetchPut(): String = (URL("https://httpbin.org/put").openConnection() as HttpURLConnection)


    .apply { requestMethod = "PUT" }

    .inputStream.bufferedReader().readText()

/*
                                Создаем фоновый поток c помощью корутины
1.rememberCoroutineScope() — создаёт область запуска корутин, привязанную к вашему экрану.
2.withContext(Dispatchers.IO) — переключает выполнение вашей функции fetchPost()
в специальный фоновый пул потоков для сети и файлов (IO = Input/Output).
*/