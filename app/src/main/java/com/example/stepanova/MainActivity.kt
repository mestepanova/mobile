package com.example.stepanova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenstruationViewModel : ViewModel() {
    private val _currentCalendar = MutableStateFlow(
        Calendar.getInstance().apply { set(2025, 1, 1) }
    )
    val currentCalendar: StateFlow<Calendar> = _currentCalendar.asStateFlow()

    private val _periodStartCalendar = MutableStateFlow(
        Calendar.getInstance().apply { set(2025, 1, 23) }
    )
    val periodStartCalendar: StateFlow<Calendar> = _periodStartCalendar.asStateFlow()

    val _highlightedDays = MutableStateFlow(mutableListOf<Calendar>())
    val highlightedDays: StateFlow<List<Calendar>> = _highlightedDays.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _menstruationDates = MutableStateFlow(
        mutableListOf(
            Calendar.getInstance().apply { set(2025, 1, 23) }
        )
    )
    val menstruationDates: StateFlow<List<Calendar>> = _menstruationDates.asStateFlow()

    private val _cycleDayAndPhase = MutableStateFlow(calculateCycleDayAndPhase(28, 5))
    val cycleDayAndPhase: StateFlow<Pair<Int, String>> = _cycleDayAndPhase.asStateFlow()

    fun onSwipe(direction: Int) {
        val newCalendar = Calendar.getInstance().apply {
            time = _currentCalendar.value.time
            add(Calendar.MONTH, if (direction == -1) 1 else -1)
        }
        _currentCalendar.value = newCalendar
    }

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value
    }

    fun onDayClicked(day: Calendar, menstruationLength: Int) {
        if (!_isEditing.value) return
        val newList = _highlightedDays.value.toMutableList()
        val newDates = _menstruationDates.value.toMutableList()
        val isAlreadyHighlighted = newList.any { it.timeInMillis == day.timeInMillis }

        if (isAlreadyHighlighted) {
            // Убираем выделение только для нажатого дня
            newList.removeAll { it.timeInMillis == day.timeInMillis }
            newDates.removeAll { it.timeInMillis == day.timeInMillis }
        } else {
            // Добавляем нажатый день и следующие (menstruationLength - 1) дней
            val daysToAdd = mutableListOf<Calendar>()
            for (i in 0 until menstruationLength) {
                val newDay = day.clone() as Calendar
                newDay.add(Calendar.DAY_OF_MONTH, i)
                if (!newList.any { it.timeInMillis == newDay.timeInMillis }) {
                    daysToAdd.add(newDay)
                }
            }
            newList.addAll(daysToAdd)
            newDates.addAll(daysToAdd)
        }

        _highlightedDays.value = newList.sortedBy { it.timeInMillis }.toMutableList()
        _menstruationDates.value = newDates.sortedBy { it.timeInMillis }.toMutableList()
    }

    private fun calculateCycleDayAndPhase(cycleLength: Int, menstruationLength: Int): Pair<Int, String> {
        val currentDate = Calendar.getInstance()
        val menstruationDates = _menstruationDates.value

        val lastMenstruationStart = menstruationDates
            .filter { it.timeInMillis <= currentDate.timeInMillis }
            .maxByOrNull { it.timeInMillis } ?: menstruationDates.first()

        val diffInMillis = currentDate.timeInMillis - lastMenstruationStart.timeInMillis
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        val cycleDay = (diffInDays % cycleLength) + 1

        val phase = when {
            cycleDay <= menstruationLength -> "Менструальная"
            cycleDay <= cycleLength / 2 -> "Фолликулярная"
            cycleDay in (cycleLength / 2 - 1)..(cycleLength / 2 + 1) -> "Овуляция"
            else -> "Лютеиновая"
        }

        return Pair(cycleDay, phase)
    }

    fun updateCycleParameters(cycleLength: Int, menstruationLength: Int) {
        _cycleDayAndPhase.value = calculateCycleDayAndPhase(cycleLength, menstruationLength)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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

            MaterialTheme(typography = montserratTypography) {
                val navController = rememberNavController()
                var cycleLength by remember { mutableStateOf(28) }
                var periodLength by remember { mutableStateOf(5) }
                val viewModel: MenstruationViewModel = viewModel()

                LaunchedEffect(cycleLength, periodLength) {
                    viewModel.updateCycleParameters(cycleLength, periodLength)
                }

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
                        MyMenstruationScreen(navController, cycleLength, periodLength, viewModel)
                    }
                    composable("phaseScreen") {
                        PhaseScreen(navController, viewModel)
                    }
                    composable("settingsScreen") {
                        SettingsScreen(navController)
                    }
                    composable("cycleScreen") {
                        CycleScreen(
                            navController = navController,
                            initialCycleLength = cycleLength,
                            onSave = { newCycleLength ->
                                cycleLength = newCycleLength
                            }
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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

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
                    fontSize = 20.sp
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
            Spacer(modifier = Modifier.height(32.dp))
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
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
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

            Spacer(modifier = Modifier.height(16.dp))

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
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
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

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun MyMenstruationScreen(
    navController: androidx.navigation.NavController,
    cycleLength: Int,
    periodLength: Int,
    viewModel: MenstruationViewModel
) {
    val currentCalendar by viewModel.currentCalendar.collectAsState()
    val periodStartCalendar by viewModel.periodStartCalendar.collectAsState()
    val highlightedDays by viewModel.highlightedDays.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val cycleDayAndPhase by viewModel.cycleDayAndPhase.collectAsState()
    val (cycleDay, _) = cycleDayAndPhase

    val swipeState = rememberSwipeableState(0)
    val swipeRange = 300f

    val todayCalendar = Calendar.getInstance()

    // Инициализация highlightedDays начальными значениями при первом запуске
    LaunchedEffect(Unit) {
        if (highlightedDays.isEmpty()) {
            val initialHighlighted = mutableListOf<Calendar>()
            val periodEndCalendar = Calendar.getInstance().apply {
                time = periodStartCalendar.time
                add(Calendar.DAY_OF_MONTH, periodLength - 1)
            }
            var currentDay = periodStartCalendar.clone() as Calendar
            while (currentDay.timeInMillis <= periodEndCalendar.timeInMillis) {
                initialHighlighted.add(currentDay.clone() as Calendar)
                currentDay.add(Calendar.DAY_OF_MONTH, 1)
            }
            viewModel._highlightedDays.value = initialHighlighted
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(16.dp)
            .swipeable(
                state = swipeState,
                anchors = mapOf(
                    0f to 0,
                    -swipeRange to -1,
                    swipeRange to 1
                ),
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(swipeState.currentValue) {
            if (swipeState.currentValue != 0) {
                viewModel.onSwipe(swipeState.currentValue)
                swipeState.snapTo(0)
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Моя Менструация",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
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
                    text = "${getMonthName(currentCalendar.get(Calendar.MONTH) + 1)} ${currentCalendar.get(Calendar.YEAR)}",
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
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
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

                val daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val firstDayCalendar = Calendar.getInstance().apply {
                    time = currentCalendar.time
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val firstDayOffset = (firstDayCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
                val weeks = 6

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (week in 0 until weeks) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (dayOfWeek in 0 until 7) {
                                val dayNumber = week * 7 + dayOfWeek - firstDayOffset + 1
                                val currentDayCalendar = if (dayNumber in 1..daysInMonth) {
                                    Calendar.getInstance().apply {
                                        time = currentCalendar.time
                                        set(Calendar.DAY_OF_MONTH, dayNumber)
                                    }
                                } else null

                                val isHighlightedDay = currentDayCalendar?.let { day ->
                                    highlightedDays.any { it.timeInMillis == day.timeInMillis }
                                } ?: false

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .border(0.5.dp, Color.Gray)
                                        .background(if (isHighlightedDay) Color.Red.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable(
                                            enabled = isEditing && dayNumber in 1..daysInMonth
                                        ) {
                                            currentDayCalendar?.let { day ->
                                                viewModel.onDayClicked(day, periodLength) // Передаем periodLength
                                            }
                                        }
                                ) {
                                    if (dayNumber in 1..daysInMonth) {
                                        Text(
                                            text = dayNumber.toString(),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
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
                    onClick = { viewModel.toggleEditing() },
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
                        text = if (isEditing) "Подтвердить" else "Редактировать",
                        fontSize = 20.sp
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
                    text = "${todayCalendar.get(Calendar.DAY_OF_MONTH)} ${getMonthName(todayCalendar.get(Calendar.MONTH) + 1)}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    textAlign = TextAlign.Left,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "$cycleDay день цикла",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
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
fun PhaseScreen(
    navController: androidx.navigation.NavController,
    viewModel: MenstruationViewModel
) {
    val cycleDayAndPhase by viewModel.cycleDayAndPhase.collectAsState()
    val (cycleDay, phase) = cycleDayAndPhase

    val backgroundColor = when (phase) {
        "Менструальная" -> Color(0xFFEBC6C6)
        "Фолликулярная" -> Color(0xFFD4EAF7)
        "Овуляция" -> Color(0xFFFAD4B5)
        "Лютеиновая" -> Color(0xFFD8C2E0)
        else -> Color(0xFFFEF7FF)
    }

    val phaseText = when (phase) {
        "Менструальная" -> "Менструация"
        "Фолликулярная" -> "Фолликулярная"
        "Овуляция" -> "Овуляция"
        "Лютеиновая" -> "Лютеиновая"
        else -> "Неизвестная фаза"
    }

    val nutritionText = when (phase) {
        "Менструальная" -> "Ешь теплые, питательные блюда, больше железа"
        "Фолликулярная" -> "Больше белка и сложных углеводов"
        "Овуляция" -> "Добавь больше овощей, зелени и полезных жиров"
        "Лютеиновая" -> "Больше белка, полезных жиров, магния, меньше сахара и соли."
        else -> "Сбалансированное питание"
    }

    val workoutText = when (phase) {
        "Менструальная" -> "Сосредоточься на легкой растяжке и ходьбе"
        "Фолликулярная" -> "Увеличивай нагрузки, тренируйся интенсивнее и дольше"
        "Овуляция" -> "Пробуй новые виды нагрузок и соревнования"
        "Лютеиновая" -> "Умеренные силовые нагрузки, йога, растяжка, меньше кардио."
        else -> "Лёгкая активность"
    }

    val productivityText = when (phase) {
        "Менструальная" -> "Делай только важное, минимизируй когнитивные нагрузки"
        "Фолликулярная" -> "Планируй сложные задачи, мозг работает продуктивнее"
        "Овуляция" -> "Используй соцактивность — договаривайся, презентуй идеи."
        "Лютеиновая" -> "Планируй заранее, больше отдыха, фокус на рутинные задачи."
        else -> "Обычная продуктивность"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
                    text = phaseText,
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
                    text = nutritionText,
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
                    text = workoutText,
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
                    text = productivityText,
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
                    onClick = { /* Логика удаления данных */ },
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
    initialCycleLength: Int,
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
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Твоя ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("длина цикла")
                    }
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }

        val days = (21..30).toList()
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
                    selectedIndex = infiniteDays[centerIndex] - 21
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
                    val isSelected = day - 21 == selectedIndex
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

        Spacer(modifier = Modifier.height(45.dp))
        Button(
            onClick = {
                onSave(selectedIndex + 21)
                navController.navigate("menstScreen")
            },
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
            Spacer(modifier = Modifier.height(80.dp))
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
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }

        val days = (3..7).toList()
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
                    selectedIndex = infiniteDays[centerIndex] - 3
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
                    val isSelected = day - 3 == selectedIndex
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

        Spacer(modifier = Modifier.height(45.dp))
        Button(
            onClick = { onSave(selectedIndex + 3) },
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

fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Январь"
        2 -> "Февраль"
        3 -> "Март"
        4 -> "Апрель"
        5 -> "Май"
        6 -> "Июнь"
        7 -> "Июль"
        8 -> "Август"
        9 -> "Сентябрь"
        10 -> "Октябрь"
        11 -> "Ноябрь"
        12 -> "Декабрь"
        else -> "Неизвестно"
    }
}