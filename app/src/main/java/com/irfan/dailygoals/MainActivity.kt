package com.irfan.dailygoals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.irfan.dailygoals.data.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val dao by lazy { AppDatabase.get(this).goalsDao() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            if (dao.weeklyGoalsSnapshot().isEmpty()) {
                dao.saveWeeklyGoal(WeeklyGoal("room-clean", "Bersihkan kamar", 1))
                dao.saveWeeklyGoal(WeeklyGoal("meals", "Makan 2 kali sehari", 2))
                dao.saveWeeklyGoal(WeeklyGoal("visit-dad", "Kerumah bapak", 1))
            }
        }
        setContent { DailyGoalsApp(dao) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyGoalsApp(dao: GoalsDao) {
    var tab by remember { mutableStateOf("Beranda") }
    val goals by dao.weeklyGoals().collectAsStateWithLifecycle(initialValue = emptyList())
    val today = LocalDate.now().toString()
    val checks by dao.checksForDate(today).collectAsStateWithLifecycle(initialValue = emptyList())
    val sessions by dao.gymSessions().collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var newGoal by remember { mutableStateOf("") }
    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("Daily Goals") }) }, bottomBar = {
            NavigationBar {
                listOf("Beranda", "Weekly", "Gym", "To-Do").forEach { item ->
                    NavigationBarItem(selected = tab == item, onClick = { tab = item }, icon = {}, label = { Text(item) })
                }
            }
        }) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Text(LocalDate.now().toString(), style = MaterialTheme.typography.titleMedium) }
                when (tab) {
                    "Beranda" -> {
                        item { Text("Daily Goals", style = MaterialTheme.typography.headlineMedium) }
                        item { Text("Fondasi aplikasi native sudah siap. Checklist harian, kalender, grafik, dan migrasi data HTML akan dibangun bertahap.") }
                        item { Text("Weekly goals: ${goals.size}") }
                    }
                    "Weekly" -> {
                        item { Text("Weekly Goals", style = MaterialTheme.typography.headlineMedium) }
                        items(goals, key = { it.id }) { goal ->
                            val checked = checks.any { it.goalId == goal.id && it.done }
                            ElevatedCard {
                                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(Modifier.weight(1f)) { Text(goal.name); Text("Target: ${goal.weeklyTarget} hari") }
                                    Checkbox(checked = checked, onCheckedChange = { yes ->
                                        scope.launch {
                                            if (yes) dao.setWeeklyCheck(WeeklyCheck(goalId = goal.id, date = today))
                                            else dao.clearWeeklyCheck(goal.id, today)
                                        }
                                    })
                                }
                            }
                        }
                        item { OutlinedTextField(value = newGoal, onValueChange = { newGoal = it }, label = { Text("Nama kegiatan baru") }, modifier = Modifier.fillMaxWidth()) }
                        item { Button(onClick = {
                            val name = newGoal.trim()
                            if (name.isNotEmpty()) {
                                scope.launch { dao.saveWeeklyGoal(WeeklyGoal(id = "goal-${System.currentTimeMillis()}", name = name, weeklyTarget = 1)) }
                                newGoal = ""
                            }
                        }, enabled = newGoal.isNotBlank()) { Text("Tambah kegiatan") } }
                    }
                    "Gym" -> {
                        item { Text("Gym Tracker", style = MaterialTheme.typography.headlineMedium) }
                        item { Text("Riwayat sesi tersimpan: ${sessions.size}") }
                        item { Text("Program latihan, detail exercise, set, beban, dan grafik progres akan dimigrasikan dari HTML pada tahap berikutnya.") }
                    }
                    else -> {
                        item { Text("To-Do List", style = MaterialTheme.typography.headlineMedium) }
                        item { Text("Layar native disiapkan. Berikutnya kita pindahkan tugas, deadline, prioritas, dan subtasks dari aplikasi HTML.") }
                    }
                }
            }
        }
    }
}
