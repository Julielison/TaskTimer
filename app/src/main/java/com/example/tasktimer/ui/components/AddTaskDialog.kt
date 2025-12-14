package com.example.tasktimer.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import com.example.tasktimer.model.Task
import com.example.tasktimer.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (String, String?, LocalDateTime, Int?, List<Subtask>, PomodoroConfig?) -> Unit,
    categories: List<Category> = emptyList(),
    pomodoroPresets: List<Pair<String, PomodoroConfig>> = emptyList(),
    initialDate: LocalDate = LocalDate.now(),
    existingTask: Task? = null
) {
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var selectedDate by remember { mutableStateOf(existingTask?.dateTime?.toLocalDate() ?: initialDate) }
    var selectedTime by remember { mutableStateOf(existingTask?.dateTime?.toLocalTime() ?: LocalTime.now()) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(existingTask?.categoryId) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    
    // Subtasks - carrega existentes se houver (agora com status de conclusão)
    var subtasks by remember { 
        mutableStateOf<List<SubtaskInput>>(
            existingTask?.subtasks?.map { 
                SubtaskInput(it.id, it.title, it.isCompleted) 
            } ?: emptyList()
        ) 
    }
    var newSubtaskTitle by remember { mutableStateOf("") }
    
    // Pomodoro - carrega configuração existente
    var enablePomodoro by remember { mutableStateOf(existingTask?.pomodoroConfig != null) }
    var selectedPomodoroPreset by remember { mutableStateOf<String?>(null) }
    var showPomodoroConfig by remember { mutableStateOf(false) }
    var customPomodoro by remember { mutableStateOf(existingTask?.pomodoroConfig ?: PomodoroConfig()) }

    // Animação para slide de baixo para cima
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 1000.dp,
        animationSpec = tween(durationMillis = 300),
        label = "slideAnimation"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .offset(y = offsetY)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = SurfaceDark,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Handle indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(
                                    TextGray.copy(alpha = 0.5f),
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }

                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (existingTask != null) "Editar Tarefa" else "Nova Tarefa",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = TextWhite,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = DividerDefaults.Thickness, color = TextGray.copy(alpha = 0.2f)
                    )

                    // Content com scroll
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        // Nome da Task
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                titleError = false
                            },
                            label = { Text("Nome da tarefa *", color = TextGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = TextGray,
                                errorBorderColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                                cursorColor = PrimaryBlue
                            ),
                            isError = titleError,
                            supportingText = if (titleError) {
                                { Text("O nome da tarefa é obrigatório", color = androidx.compose.ui.graphics.Color(0xFFD32F2F)) }
                            } else null
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Descrição
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descrição (opcional)", color = TextGray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = TextGray,
                                cursorColor = PrimaryBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Data e Horário
                        Text(
                            text = "Data e Horário",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextWhite
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text("📅", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { showTimePicker = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextWhite
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text("🕐", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Subtasks
                        Text(
                            text = "Subtarefas",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        subtasks.forEach { subtask ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = subtask.isCompleted,
                                    onCheckedChange = { isChecked ->
                                        subtasks = subtasks.map { 
                                            if (it.id == subtask.id) {
                                                it.copy(isCompleted = isChecked)
                                            } else {
                                                it
                                            }
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        uncheckedColor = TextGray,
                                        checkedColor = PrimaryBlue
                                    )
                                )
                                
                                Text(
                                    text = subtask.title,
                                    color = if (subtask.isCompleted) TextGray.copy(alpha = 0.6f) else TextWhite,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp),
                                    fontSize = 15.sp,
                                    textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                                
                                IconButton(
                                    onClick = {
                                        subtasks = subtasks.filter { it.id != subtask.id }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover",
                                        tint = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newSubtaskTitle,
                                onValueChange = { newSubtaskTitle = it },
                                label = { Text("Nova subtarefa", color = TextGray) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = TextGray,
                                    cursorColor = PrimaryBlue
                                )
                            )

                            FilledIconButton(
                                onClick = {
                                    if (newSubtaskTitle.isNotBlank()) {
                                        subtasks = subtasks + SubtaskInput(
                                            id = (subtasks.maxOfOrNull { it.id } ?: 0) + 1,
                                            title = newSubtaskTitle.trim(),
                                            isCompleted = false
                                        )
                                        newSubtaskTitle = ""
                                    }
                                },
                                enabled = newSubtaskTitle.isNotBlank(),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = if (newSubtaskTitle.isNotBlank()) PrimaryBlue else TextGray,
                                    contentColor = androidx.compose.ui.graphics.Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Pomodoro
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🍅", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pomodoro Timer",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Switch(
                                checked = enablePomodoro,
                                onCheckedChange = { enablePomodoro = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                                    checkedTrackColor = PrimaryBlue,
                                    uncheckedThumbColor = TextGray,
                                    uncheckedTrackColor = SurfaceDark
                                )
                            )
                        }

                        if (enablePomodoro) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Presets de Pomodoro
                            if (pomodoroPresets.isNotEmpty()) {
                                pomodoroPresets.forEach { (name, config) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedPomodoroPreset == name,
                                            onClick = {
                                                selectedPomodoroPreset = name
                                                customPomodoro = config
                                                showPomodoroConfig = false
                                            },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = PrimaryBlue,
                                                unselectedColor = TextGray
                                            )
                                        )
                                        Column(modifier = Modifier.padding(start = 8.dp)) {
                                            Text(
                                                text = name,
                                                color = TextWhite,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${config.workDurationMinutes}min • ${config.breakDurationMinutes}min pausa • ${config.totalPomodoros}x",
                                                color = TextGray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Personalizar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = showPomodoroConfig,
                                    onClick = {
                                        showPomodoroConfig = true
                                        selectedPomodoroPreset = null
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PrimaryBlue,
                                        unselectedColor = TextGray
                                    )
                                )
                                Text(
                                    text = "Personalizar",
                                    color = TextWhite,
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (showPomodoroConfig) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        PomodoroConfigField(
                                            label = "Tempo de trabalho (min)",
                                            value = customPomodoro.workDurationMinutes,
                                            onValueChange = { customPomodoro = customPomodoro.copy(workDurationMinutes = it) }
                                        )
                                        PomodoroConfigField(
                                            label = "Pausa curta (min)",
                                            value = customPomodoro.breakDurationMinutes,
                                            onValueChange = { customPomodoro = customPomodoro.copy(breakDurationMinutes = it) }
                                        )
                                        PomodoroConfigField(
                                            label = "Pausa longa (min)",
                                            value = customPomodoro.longBreakDurationMinutes,
                                            onValueChange = { customPomodoro = customPomodoro.copy(longBreakDurationMinutes = it) }
                                        )
                                        PomodoroConfigField(
                                            label = "Pomodoros até pausa longa",
                                            value = customPomodoro.pomodorosUntilLongBreak,
                                            onValueChange = { customPomodoro = customPomodoro.copy(pomodorosUntilLongBreak = it) }
                                        )
                                        PomodoroConfigField(
                                            label = "Total de pomodoros",
                                            value = customPomodoro.totalPomodoros,
                                            onValueChange = { customPomodoro = customPomodoro.copy(totalPomodoros = it) }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Categoria
                        if (categories.isNotEmpty()) {
                            Text(
                                text = "Categoria",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            categories.forEach { category ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedCategoryId == category.id,
                                        onClick = {
                                            selectedCategoryId = if (selectedCategoryId == category.id) null else category.id
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = PrimaryBlue,
                                            unselectedColor = TextGray
                                        )
                                    )
                                    Text(
                                        text = category.name,
                                        color = TextWhite,
                                        modifier = Modifier.padding(start = 8.dp),
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp)) // Espaço para os botões fixos
                    }

                    // Botões fixos na parte inferior
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceDark,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextWhite
                                )
                            ) {
                                Text("Cancelar", modifier = Modifier.padding(vertical = 8.dp))
                            }

                            Button(
                                onClick = {
                                    if (title.isBlank()) {
                                        titleError = true
                                    } else {
                                        val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                                        val finalSubtasks = subtasks.mapIndexed { index, input ->
                                            Subtask(
                                                id = input.id,
                                                taskId = 0,
                                                title = input.title,
                                                isCompleted = input.isCompleted,
                                                order = index
                                            )
                                        }
                                        val finalPomodoro = if (enablePomodoro) customPomodoro else null

                                        onSave(
                                            title.trim(),
                                            description.trim().takeIf { it.isNotBlank() },
                                            dateTime,
                                            selectedCategoryId,
                                            finalSubtasks,
                                            finalPomodoro
                                        )
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue,
                                    contentColor = androidx.compose.ui.graphics.Color.White
                                )
                            ) {
                                Text("Salvar", modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK", color = PrimaryBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = TextGray)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = SurfaceDark
            )
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.toEpochDay() * 86400000
                ),
                colors = DatePickerDefaults.colors(
                    containerColor = SurfaceDark,
                    titleContentColor = TextWhite,
                    headlineContentColor = TextWhite,
                    weekdayContentColor = TextGray,
                    dayContentColor = TextWhite,
                    selectedDayContainerColor = PrimaryBlue,
                    todayContentColor = PrimaryBlue,
                    todayDateBorderColor = PrimaryBlue
                )
            )
        }
    }

    // TimePicker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = { hour, minute ->
                selectedTime = LocalTime.of(hour, minute)
                showTimePicker = false
            }
        )
    }
}

@Composable
private fun PomodoroConfigField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { if (value > 1) onValueChange(value - 1) }
            ) {
                Text("-", color = PrimaryBlue, fontSize = 20.sp)
            }
            
            Text(
                text = value.toString(),
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.widthIn(min = 30.dp)
            )
            
            IconButton(
                onClick = { onValueChange(value + 1) }
            ) {
                Text("+", color = PrimaryBlue, fontSize = 20.sp)
            }
        }
    }
}

private data class SubtaskInput(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
)

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(LocalTime.now().hour) }
    var selectedMinute by remember { mutableIntStateOf(LocalTime.now().minute) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Selecionar Horário", color = TextWhite) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour picker
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hora", color = TextGray, fontSize = 12.sp)
                    Text(
                        text = String.format("%02d", selectedHour),
                        color = TextWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(onClick = { selectedHour = (selectedHour - 1 + 24) % 24 }) {
                            Text("-", color = PrimaryBlue, fontSize = 24.sp)
                        }
                        IconButton(onClick = { selectedHour = (selectedHour + 1) % 24 }) {
                            Text("+", color = PrimaryBlue, fontSize = 24.sp)
                        }
                    }
                }

                Text(":", color = TextWhite, fontSize = 32.sp, modifier = Modifier.padding(horizontal = 16.dp))

                // Minute picker
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Minuto", color = TextGray, fontSize = 12.sp)
                    Text(
                        text = String.format("%02d", selectedMinute),
                        color = TextWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(onClick = { selectedMinute = (selectedMinute - 1 + 60) % 60 }) {
                            Text("-", color = PrimaryBlue, fontSize = 24.sp)
                        }
                        IconButton(onClick = { selectedMinute = (selectedMinute + 1) % 60 }) {
                            Text("+", color = PrimaryBlue, fontSize = 24.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedHour, selectedMinute) }) {
                Text("OK", color = PrimaryBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar", color = TextGray)
            }
        },
        containerColor = SurfaceDark
    )
}
