package com.example.tasktimer.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info // Trocado Assessment por Info (nativo)
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.model.Task
import com.example.tasktimer.ui.theme.*

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val overdueTasks by viewModel.overdueTasks.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()

    // Estado para rolagem da tela inteira
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState) // Habilita rolagem na tela toda
        ) {
            TaskSection(title = "Vencidas", tasks = overdueTasks)
            Spacer(modifier = Modifier.height(16.dp))
            TaskSection(title = "Hoje", tasks = todayTasks)
        }
    }
}

@Composable
fun TaskSection(title: String, tasks: List<Task>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // CORREÇÃO: Usamos forEach em vez de LazyColumn aqui
            // LazyColumn não funciona bem dentro de containers roláveis
            Column {
                tasks.forEach { task ->
                    TaskItem(task)
                }
            }
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = TextWhite,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Hoje",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Usei Icons.Default.Info para garantir compatibilidade
        // Se quiser Assessment, adicione a dependência: material-icons-extended
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Stats",
            tint = TextWhite,
            modifier = Modifier.size(28.dp)
        )
    }
}

// O restante (TaskItem, HomeBottomBar) permanece igual...
@Composable
fun TaskItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = {},
                colors = CheckboxDefaults.colors(
                    uncheckedColor = TextGray,
                    checkedColor = PrimaryBlue
                )
            )
            Text(
                text = task.title,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = task.timeOrDate,
            color = task.timeColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HomeBottomBar() {
    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextGray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Check, contentDescription = null) },
            label = { Text("Tasks") },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextWhite,
                selectedTextColor = TextWhite,
                indicatorColor = SelectedNav,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            label = { Text("Calendário") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Pesquisar") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
    }
}