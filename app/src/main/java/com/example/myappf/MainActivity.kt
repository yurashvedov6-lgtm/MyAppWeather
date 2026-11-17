package com.example.myappf


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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWeather(name = "android")
        }
    }
}

@Composable
fun MyWeather(name: String) {
    val state = remember { mutableStateOf("unknow") }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()//•  растягивает колонку на 100% ширины и высоты экрана.
    ) {
        Box(
            contentAlignment = Alignment.Center,// распологает по центру
            modifier = Modifier
                .fillMaxWidth()//занимает всю ширину экрана
                .fillMaxHeight(0.5f)// половину высоты
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
            contentAlignment = Alignment.BottomCenter,//Распологает по центру внизу
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color.Yellow)
                .padding(bottom = 20.dp)
        ) {
            Button(
                onClick = {
                    getResalt(city = "London", state = state, context = context)
                },
                modifier = Modifier
                    .fillMaxWidth()//кнопка по ширине
            ) {
                Text(
                    text = "push me "
                )

            }

        }
    }
}

private fun getResalt(city: String, state: MutableState<String>, context: Context) {


}

/*
1. Инициализация при запуске (сверху вниз)

Когда приложение открывается, данные передаются по цепочке сверху вниз:
• Старт из MainActivity: Метод onCreate вызывает MyWeather(name = "android"). Строка "android" заходит в функцию MyWeather как обычный неизменяемый параметр (name).
• Создание ячейки памяти (state): Функция remember { mutableStateOf("unknow") } выделяет в памяти Compose специальную «коробку» (MutableState), кладет туда слово "unknow" и запоминает её между перерисовками экрана.
• Первая отрисовка (Композиция): Компонент Text заглядывает в name и в state.value, склеивает их и рисует на экране текст.

2. Действие пользователя (снизу вверх)

Когда пользователь нажимает на кнопку, запускается обратный процесс передачи управления:
• Перехват клика: Кнопка Button вызывает блок onClick = { ... }.
• Передача «ключей» в getResalt: В функцию getResalt передаются три вещи:
	1. city = "London" — данные (что именно нужно найти).
	2. state — ссылка на ту самую «коробку» в памяти (а не просто копия текста).
	3. context — доступ к системным ресурсам Android для выполнения фоновой работы (например, выхода в интернет).

3. Изменение данных и автоматическое обновление (Рекомпозиция)

Внутри getResalt происходит главная магия реактивного интерфейса:
• Запись нового значения: Когда getResalt выполнит свою работу (например, получит погоду с сервера), она запишет результат прямо в полученную «коробку»: state.value = "15°C".
• Сигнал об изменении: Сама «коробка» MutableState устроена так, что при изменении .value она автоматически рассылает сигнал всем элементам интерфейса, которые её читают.
• Перерисовка только нужного элемента: Jetpack Compose видит, что значение внутри state изменилось. Он не перезапускает весь экран заново, а точечно перерисовывает только тот компонент Text, который привязан к state.value, подставляя туда "15°C".

Короткая схема движения данных

1. Данные вниз: MainActivity → MyWeather → Text (рисует начальное значение "unknow").
2. Событие вверх: Нажатие на Button → вызов getResalt(city, state, context).
3. Обновление состояния: getResalt меняет state.value → Text автоматически замечает это и обновляется на экране.
Хотите наполнить функцию getResalt реальной бизнес-логикой — например, подключить библиотеку Volley и получить настоящую погоду с сервера WeatherAPI?
*/