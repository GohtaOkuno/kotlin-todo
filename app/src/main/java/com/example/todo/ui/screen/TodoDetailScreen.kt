package com.example.todo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.data.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    task: Task,
    onBackClick: () -> Unit,
    onToggleTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onEditTask: (String) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    
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
}