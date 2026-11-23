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

    var bgThreadName by remember { mutableStateOf("Пока не создан") }


    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {

        Text(text = "Фоновый поток: $bgThreadName")


        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {


                val myThread = Thread {
                    try {//строхуемся от от ошибок при запросе
                        /* Стандартный URL(...).readText() умеет делать только простой GET-запрос  */
                        val response = URL("https://google.com").readText().take(200)

                        bgThreadName = response //передал запрос в переменную состояния

                    }catch (e: Exception){
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
/*
                                Создаем фоновый поток в ручную
       1.с помощью переменной myThread {} поместили его в действие onClick

*/