package com.example.stepanova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Определяем семейство шрифтов Montserrat
            val montserratFontFamily = FontFamily(
                Font(R.font.montserrat_regular, FontWeight.Normal),
                Font(R.font.montserrat_bold, FontWeight.Bold)
            )

            val montserratTypography = Typography(
                displayLarge = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 57.sp,
                    lineHeight = 64.sp,
                    letterSpacing = (-0.25).sp
                ),
                displayMedium = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp,
                    lineHeight = 52.sp
                ),
                displaySmall = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    lineHeight = 44.sp
                ),
                headlineLarge = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp
                ),
                headlineMedium = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                headlineSmall = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                ),
                titleLarge = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp
                ),
                titleMedium = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.15.sp
                ),
                titleSmall = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.1.sp
                ),
                bodyLarge = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.5.sp
                ),
                bodyMedium = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.25.sp
                ),
                bodySmall = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.4.sp
                ),
                labelLarge = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.1.sp
                ),
                labelMedium = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                labelSmall = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp
                )
            )

            // Применяем MaterialTheme с кастомной типографией
            MaterialTheme(typography = montserratTypography) {
                val navController = rememberNavController()
                var cycleLength by remember { mutableStateOf(28) }
                var periodLength by remember { mutableStateOf(5) }

                NavHost(
                    navController = navController,
                    startDestination = "registration"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginClick = { navController.navigate("myMenstruation") },
                            onRegisterClick = { navController.navigate("registration") }
                        )
                    }
                    composable("registration") {
                        RegistrationScreen(
                            onRegisterClick = { navController.navigate("myMenstruation") },
                            onLoginClick = { navController.navigate("login") }
                        )
                    }
                    composable("myMenstruation") {
                        MyMenstruationScreen(navController)
                    }
                    composable("phaseScreen") {
                        PhaseScreen(navController)
                    }
                    composable("settingsScreen") {
                        SettingsScreen(navController)
                    }
                    composable("cycleScreen") {
                        CycleScreen(
                            navController = navController,
                            initialCycleLength = cycleLength
                        )
                    }
                    composable("menstScreen") {
                        MenstScreen(
                            navController = navController,
                            initialPeriodLength = periodLength,
                            onSave = { newPeriodLength ->
                                periodLength = newPeriodLength
                                navController.navigate("settingsScreen") {
                                    popUpTo("settingsScreen") { inclusive = false }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegistrationScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 78.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Создать аккаунт\n")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(" в \"Дневник цикла\"")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Поле для Email
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .width(348.dp)
                    .height(45.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                placeholder = {
                    Text(
                        text = "Ваш Email",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Отступ между полями

            // Поле для пароля
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .width(348.dp)
                    .height(45.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                placeholder = {
                    Text(
                        text = "Ваш Пароль",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Отступ между полями

            // Поле для подтверждения пароля
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier
                    .width(348.dp)
                    .height(45.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                placeholder = {
                    Text(
                        text = "Подтвердите Ваш Пароль",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onRegisterClick,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Зарегистрироваться",
                    fontSize = 20.sp // Увеличиваем шрифт до 20 sp
                )
            }

            Text(
                text = buildAnnotatedString {
                    append("Есть аккаунт? ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Вход")
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 32.dp)
                    .clickable(onClick = onLoginClick),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 78.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Добро пожаловать\n")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("в \"Дневник цикла\"")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp)) // Опускаем текст ниже
            Text(
                text = "Введите свой Email и пароль",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Поле для Email
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .width(348.dp)
                    .height(45.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                placeholder = {
                    Text(
                        text = "Ваш Email",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), // Уменьшаем шрифт
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth() // Центрируем текст
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Center) // Центрируем вводимый текст
            )

            Spacer(modifier = Modifier.height(16.dp)) // Отступ между полями

            // Поле для пароля
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .width(348.dp)
                    .height(45.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                placeholder = {
                    Text(
                        text = "Ваш Пароль",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), // Уменьшаем шрифт
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth() // Центрируем текст
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Center) // Центрируем вводимый текст
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Войти",
                    fontSize = 20.sp
                )
            }

            Text(
                text = buildAnnotatedString {
                    append("Нет аккаунта? ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Регистрация")
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 32.dp)
                    .clickable(onClick = onRegisterClick),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { navController.navigate("phaseScreen") }) {
            Icon(
                painter = painterResource(id = R.drawable.curcycle),
                contentDescription = "Календарь",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
        IconButton(onClick = { navController.navigate("myMenstruation") }) {
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Редактировать",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
        IconButton(onClick = { navController.navigate("settingsScreen") }) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Настройки",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun MyMenstruationScreen(navController: androidx.navigation.NavController) {
    var isAddNoteClicked by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Моя Менструация",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp), // Заголовок 32 sp
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Февраль 2025",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Дни недели 20 sp
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Divider(
                    color = Color.Black,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val daysInFebruary = 28
                val firstDayOffset = 5
                val weeks = 6

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    for (week in 0 until weeks) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (dayOfWeek in 0 until 7) {
                                val dayNumber = week * 7 + dayOfWeek - firstDayOffset + 1
                                val isHighlighted = dayNumber in 3..7
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .border(0.5.dp, Color.Gray)
                                        .background(if (isHighlighted) Color.Red.copy(alpha = 0.2f) else Color.Transparent)
                                ) {
                                    if (dayNumber in 1..daysInFebruary) {
                                        Text(
                                            text = dayNumber.toString(),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Числа в календаре 20 sp
                                            modifier = Modifier.align(Alignment.Center),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        isAddNoteClicked = !isAddNoteClicked
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isAddNoteClicked) "Подтвердить" else "Редактировать",
                        fontSize = 20.sp // Кнопка 20 sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "23 февраля",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp), // Панель между кнопками 16 sp
                    textAlign = TextAlign.Left,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "1 день цикла",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp), // Панель между кнопками 16 sp
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.Start)
                )
            }

            Button(
                onClick = { navController.navigate("cycleScreen") },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Изменить базовый цикл",
                    fontSize = 20.sp
                )
            }
        }

        BottomNavigationBar(navController)
    }
}

@Composable
fun PhaseScreen(navController: androidx.navigation.NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBC6C6))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Моя ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("фаза")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Фаза",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Менструация",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Питание",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Ешь теплые, питательные блюда, больше железа",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Тренировки",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Сосредоточься на легкой растяжке и ходьбе",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Продуктивность",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Делай только важное, минимизируй когнитивные нагрузки",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomNavigationBar(navController)
    }
}


@Composable
fun SettingsScreen(navController: androidx.navigation.NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
        ) {
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("cycleScreen") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(start = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cycle),
                            contentDescription = "Изменить цикл",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Изменить базовый цикл",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp
                        )
                    }
                }

                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Button(
                    onClick = { },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(start = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Удалить данные",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Удалить все данные",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp
                        )
                    }
                }

                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Button(
                    onClick = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(start = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Выйти",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Выйти",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        BottomNavigationBar(navController)
    }
}

@Composable
fun CycleScreen(
    navController: androidx.navigation.NavController,
    initialCycleLength: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // Отступ 80 пикселей сверху
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Твоя ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("длина цикла")
                    }
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp), // Размер шрифта 32 sp
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center, // Центрирование текста
                color = Color.Black
            )
        }

        val days = (1..31).toList()
        val lazyListState = rememberLazyListState()
        var selectedIndex by remember { mutableStateOf(days.indexOf(initialCycleLength)) }

        val infiniteDays = remember {
            List(1000) { index ->
                days[index % days.size]
            }
        }
        val offset = infiniteDays.size / 2 - days.size / 2

        LaunchedEffect(Unit) {
            lazyListState.scrollToItem(offset + selectedIndex - 2)
        }

        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.firstVisibleItemIndex }
                .collect { firstVisibleIndex ->
                    val centerIndex = firstVisibleIndex + 2
                    selectedIndex = infiniteDays[centerIndex] - 1
                }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(infiniteDays) { index, day ->
                    val isSelected = day - 1 == selectedIndex
                    Text(
                        text = "$day дней",
                        fontSize = 20.sp,
                        style = if (isSelected) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        else MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .then(
                                if (isSelected) Modifier.background(Color.Black, RoundedCornerShape(8.dp))
                                else Modifier
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Button(
            onClick = { navController.navigate("menstScreen") },
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Далее",
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun MenstScreen(
    navController: androidx.navigation.NavController,
    initialPeriodLength: Int,
    onSave: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // Отступ 80 пикселей сверху
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Твоя ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("длина менструации")
                    }
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }

        val days = (1..7).toList()
        val lazyListState = rememberLazyListState()
        var selectedIndex by remember { mutableStateOf(days.indexOf(initialPeriodLength)) }
        val infiniteDays = remember {
            List(1000) { index ->
                days[index % days.size]
            }
        }
        val offset = infiniteDays.size / 2 - days.size / 2

        LaunchedEffect(Unit) {
            lazyListState.scrollToItem(offset + selectedIndex - 2)
        }

        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.firstVisibleItemIndex }
                .collect { firstVisibleIndex ->
                    val centerIndex = firstVisibleIndex + 2
                    selectedIndex = infiniteDays[centerIndex] - 1
                }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(infiniteDays) { index, day ->
                    val isSelected = day - 1 == selectedIndex
                    Text(
                        text = "$day дней",
                        fontSize = 20.sp,
                        style = if (isSelected) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        else MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .then(
                                if (isSelected) Modifier.background(Color.Black, RoundedCornerShape(8.dp))
                                else Modifier
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Поднимаем кнопку на 60 пикселей от низа
        Spacer(modifier = Modifier.height(45.dp))
        Button(
            onClick = { onSave(selectedIndex + 1) },
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Сохранить",
                fontSize = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    MaterialTheme {
        RegistrationScreen(
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyMenstruationScreenPreview() {
    MaterialTheme {
        MyMenstruationScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun PhaseScreenPreview() {
    MaterialTheme {
        PhaseScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun CycleScreenPreview() {
    MaterialTheme {
        CycleScreen(rememberNavController(), 28)
    }
}

@Preview(showBackground = true)
@Composable
fun MenstScreenPreview() {
    MaterialTheme {
        MenstScreen(rememberNavController(), 5, {})
    }
}