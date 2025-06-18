package com.example.todo

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
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