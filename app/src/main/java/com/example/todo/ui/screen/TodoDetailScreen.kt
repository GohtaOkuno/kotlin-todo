package com.example.todo.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    task: Task,
    onBackClick: () -> Unit,
    onToggleTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onEditTask: (String) -> Unit = {},
    onPriorityChange: (Priority) -> Unit = {},
    onDueDateChange: (Date?) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dueDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("タスク詳細") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "編集",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "削除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // タスクステータスカード
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ステータスバッジ
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (task.isDone) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (task.isDone) "完了" else "未完了",
                            tint = if (task.isDone) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (task.isDone) "完了済み" else "未完了",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (task.isDone) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.outline
                        )
                    }
                    
                    // タスクタイトル
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                        color = if (task.isDone)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    // 優先度表示
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "優先度:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when (task.priority) {
                                    Priority.HIGH -> MaterialTheme.colorScheme.error
                                    Priority.NORMAL -> MaterialTheme.colorScheme.primary
                                    Priority.LOW -> MaterialTheme.colorScheme.secondary
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = task.priority.displayName,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    
                    // 締切日表示
                    task.dueDate?.let { dueDate ->
                        val now = Date()
                        val calendar = Calendar.getInstance()
                        calendar.time = now
                        calendar.add(Calendar.DAY_OF_YEAR, 3)
                        val threeDaysLater = calendar.time
                        
                        val (dueDateText, dueDateColor) = when {
                            dueDate.before(now) -> "期限切れ: ${dueDateFormat.format(dueDate)}" to MaterialTheme.colorScheme.error
                            dueDate.before(threeDaysLater) -> "締切: ${dueDateFormat.format(dueDate)}" to Color(0xFFFF9800) // Orange
                            else -> "締切: ${dueDateFormat.format(dueDate)}" to MaterialTheme.colorScheme.outline
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "締切日",
                                tint = dueDateColor
                            )
                            Text(
                                text = dueDateText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = dueDateColor,
                                fontWeight = if (dueDate.before(now)) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    
                    // タスクID
                    Text(
                        text = "ID: ${task.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            // アクションボタン
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "アクション",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Button(
                        onClick = onToggleTask,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (task.isDone)
                                MaterialTheme.colorScheme.outline
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (task.isDone) Icons.Default.Edit else Icons.Default.Check,
                            contentDescription = if (task.isDone) "未完了にする" else "完了にする"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (task.isDone) "未完了にする" else "完了にする"
                        )
                    }
                }
            }
            
            // 優先度変更セクション
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "優先度設定",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Priority.values().forEach { priority ->
                            val isSelected = task.priority == priority
                            Button(
                                onClick = { if (!isSelected) onPriorityChange(priority) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) {
                                        when (priority) {
                                            Priority.HIGH -> MaterialTheme.colorScheme.error
                                            Priority.NORMAL -> MaterialTheme.colorScheme.primary
                                            Priority.LOW -> MaterialTheme.colorScheme.secondary
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    }
                                ),
                                elevation = if (isSelected) {
                                    ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                } else {
                                    ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                }
                            ) {
                                Text(
                                    text = priority.displayName,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // 締切日設定セクション
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "締切日設定",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showDueDatePicker = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "日付選択"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = task.dueDate?.let { dueDateFormat.format(it) } ?: "日付を選択"
                            )
                        }
                        
                        if (task.dueDate != null) {
                            Button(
                                onClick = { onDueDateChange(null) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.outline
                                )
                            ) {
                                Text("クリア")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // タスク詳細情報
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "詳細情報",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "作成日時:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dateFormat.format(task.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    task.dueDate?.let { dueDate ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "締切日時:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dueDateFormat.format(dueDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 削除確認ダイアログ
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("タスクを削除") },
            text = { Text("「${task.title}」を削除しますか？この操作は元に戻せません。") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteTask()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
    
    // 編集ダイアログ
    if (showEditDialog) {
        var editText by remember { mutableStateOf(task.title) }
        
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("タスクを編集") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    label = { Text("タスクタイトル") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editText.isNotBlank()) {
                            onEditTask(editText)
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
    
    // 日付選択ダイアログ
    if (showDueDatePicker) {
        val calendar = Calendar.getInstance()
        task.dueDate?.let {
            calendar.time = it
        }
        
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                onDueDateChange(newCalendar.time)
                showDueDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
        showDueDatePicker = false
    }
}