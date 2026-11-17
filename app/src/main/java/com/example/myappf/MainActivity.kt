package com.example.myappf


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.fillMaxSize()//•  растягивает колонку на 100% ширины и высоты экрана.
            ) {
                Box(
                    contentAlignment = Alignment.Center,// распологает по центру
                    modifier = Modifier
                        .fillMaxWidth()//занимает всю ширину экрана
                        .fillMaxHeight(0.5f)// половину высоты
                        .background(color = Color.Blue)
                ){
                    Text(text="my text",
                        fontSize = 20.sp,
                    modifier = Modifier
                        .background(color = Color.Gray)

                    )
                }
                Box(
                    contentAlignment = Alignment.BottomCenter,//Распологает по центру внизу
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(color = Color.Yellow)
                        .padding(bottom = 20.dp)
                ){
                    Button(onClick = {}){
                        Text(text = "push me ")
                    }

                }
            }
        }
    }
}



