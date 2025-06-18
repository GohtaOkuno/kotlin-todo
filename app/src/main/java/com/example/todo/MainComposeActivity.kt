package com.example.todo

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.todo.ui.theme.KotlinTodoTheme

class MainComposeActivity : ComponentActivity() {
    
    private val taskViewModel: TaskViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // NoActionBarテーマに確実に切り替え
        setTheme(R.style.Theme_KotlinTodo)
        
        // スプラッシュスクリーンの終了アニメーション
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.view,
                "translationY",
                0f,
                -splashScreenView.view.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 500L
            
            slideUp.doOnEnd { splashScreenView.remove() }
            slideUp.start()
        }
        
        setContent {
            KotlinTodoTheme {
                TodoApp(taskViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.observeAsState(emptyList())
    var taskText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Kotlin Todo",
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 入力フィールドとボタン
            TaskInputSection(
                taskText = taskText,
                onTaskTextChange = { taskText = it },
                onAddTask = {
                    if (taskText.isNotBlank()) {
                        viewModel.addTask(taskText)
                        taskText = ""
                        keyboardController?.hide()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // タスクリスト
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { viewModel.toggleTaskDone(task.id) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskInputSection(
    taskText: String,
    onTaskTextChange: (String) -> Unit,
    onAddTask: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPressed by remember { mutableStateOf(false) }
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = taskText,
            onValueChange = onTaskTextChange,
            label = { Text("新しいタスクを入力") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onAddTask()
                    keyboardController?.hide()
                }
            ),
            singleLine = true
        )
        
        Button(
            onClick = {
                isPressed = true
                onAddTask()
                isPressed = false
            },
            modifier = Modifier.scale(buttonScale)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "追加",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("追加")
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }
    
    // 新しいアイテムの登場アニメーション
    val slideInOffset by animateIntAsState(
        targetValue = if (isVisible) 0 else 300,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "slide_in"
    )
    
    // 削除アニメーション
    val deleteOffset by animateIntAsState(
        targetValue = if (isDeleting) 300 else 0,
        animationSpec = tween(250),
        label = "delete_offset"
    )
    
    val deleteAlpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(250),
        label = "delete_alpha"
    )
    
    // 完了時のアニメーション
    var bounceOffset by remember { mutableStateOf(0f) }
    val bounceAnim = remember { Animatable(0f) }
    
    LaunchedEffect(task.isDone) {
        if (task.isDone) {
            bounceAnim.animateTo(
                targetValue = -20f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            bounceAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = (slideInOffset + deleteOffset).toFloat()
                alpha = deleteAlpha
                translationY = bounceAnim.value
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { onToggle() }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    color = if (task.isDone) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            }
            
            IconButton(
                onClick = {
                    isDeleting = true
                    // アニメーション後に削除
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(250)
                        onDelete()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    
    // 登場アニメーション
    LaunchedEffect(Unit) {
        isVisible = true
    }
}