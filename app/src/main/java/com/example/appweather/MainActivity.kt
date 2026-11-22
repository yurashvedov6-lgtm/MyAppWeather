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
                    val name = Thread.currentThread().name // это строка запращив имя

                    Thread.sleep(2000)
                    bgThreadName = name

                }
                myThread.start()
            }
        ) {
            Text("Создать Thread вручную")
        }
    }
}
/*
                                Создаем фоновый поток в ручную
       1.с помощью переменной myThread {} поместили его в действие onClick
       2.bgThreadName эта переменная в нее записали "пока не создан поток "
       затем ее поместили сверху в элемент Text
       3. После задержки Thread.sleep(millis=2000)
       4.Затем ей присвоили переменную name в самом потоке .
       5.При нажатии кнопки создаются поочереди параллельные потоки
*/