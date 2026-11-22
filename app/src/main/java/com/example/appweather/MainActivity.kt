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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Точка входа: запускаем наш экран
            SimpleMainThreadBrick()
        }
    }
}

@Composable
fun SimpleMainThreadBrick() {
    // 1. Переменная состояния живет в главном потоке
    var counter by remember { mutableStateOf(0) }
    val threadName = Thread.currentThread().name // Вернет "main"

    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)) {
        Text(text = "Счетчик: $counter")
        Text(text = "Работаем в потоке: $threadName")

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Нажатие кнопки тоже срабатывает мгновенно в главном потоке
        Button(
            onClick = {
                counter++ // Обычное синхронное действие: изменили число -> экран перерисовался
            }
        ) {
            Text("Нажать (+1)")
        }
    }
}
/*
                    //================ в этом коде начал изучать ООП ====================//
                     Обяснение перем threadName c точки ООП.
                     так как здесь на самом деле нет наследования в самой записи,
                     но есть работа с классом,
                      статическим методом, объектом (экземпляром) и свойством.
                      В этом коде запустил самый простой главный поток main ив строке
                      val threadName = Thread.currentThread().name // Вернет "main"
                      с помощью главного класса потока и его статистического метода currentThread
                и поля name посмотрел как он называется и в нем (Thread ) потоке крутится мой
                код кнопки.

*/