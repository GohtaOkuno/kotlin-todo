package com.example.todo

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // 通常のテーマに切り替え
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
        
        setContentView(R.layout.activity_main)
        
        // ツールバーをセットアップ
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        // WindowInsetsを処理してノッチとの重なりを防ぐ - AppBarLayoutのみに適用
        val appBarLayout = findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.app_bar_layout)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        
        setupRecyclerView()
        setupViews()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskToggle = { taskId ->
                taskViewModel.toggleTaskDone(taskId)
            },
            onTaskDelete = { taskId ->
                taskViewModel.deleteTask(taskId)
            }
        )
        
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupViews() {
        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        
        buttonAdd.setOnClickListener {
            addTask(editTextTask)
        }
        
        editTextTask.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addTask(editTextTask)
                true
            } else {
                false
            }
        }
    }
    
    private fun addTask(editTextTask: EditText) {
        val taskTitle = editTextTask.text.toString()
        if (taskTitle.isNotBlank()) {
            // 入力フィールドのアニメーション
            val scaleDown = ObjectAnimator.ofFloat(editTextTask, "scaleX", 1f, 0.95f)
            scaleDown.duration = 50
            val scaleUp = ObjectAnimator.ofFloat(editTextTask, "scaleX", 0.95f, 1f)
            scaleUp.duration = 50
            
            scaleDown.start()
            scaleDown.doOnEnd { scaleUp.start() }
            
            taskViewModel.addTask(taskTitle)
            editTextTask.text.clear()
        }
    }
    
    private fun observeViewModel() {
        taskViewModel.tasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }
}