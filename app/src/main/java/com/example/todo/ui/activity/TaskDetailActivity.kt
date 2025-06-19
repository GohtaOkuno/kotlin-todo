package com.example.todo.ui.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.R
import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import com.example.todo.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {
    
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var editTextTitle: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var editTextDueDate: EditText
    private lateinit var buttonClearDueDate: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button
    
    private var taskId: Int = -1
    private var currentTask: Task? = null
    private var selectedDueDate: Date? = null
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    
    companion object {
        private const val EXTRA_TASK_ID = "extra_task_id"
        
        fun newIntent(context: Context, taskId: Int): Intent {
            return Intent(context, TaskDetailActivity::class.java).apply {
                putExtra(EXTRA_TASK_ID, taskId)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        
        initViews()
        setupSpinner()
        
        taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        if (taskId != -1) {
            loadTask()
        }
        
        setupClickListeners()
    }
    
    private fun initViews() {
        editTextTitle = findViewById(R.id.editTextTitle)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        editTextDueDate = findViewById(R.id.editTextDueDate)
        buttonClearDueDate = findViewById(R.id.buttonClearDueDate)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)
    }
    
    private fun setupSpinner() {
        val priorities = Priority.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter
    }
    
    private fun loadTask() {
        CoroutineScope(Dispatchers.IO).launch {
            val task = viewModel.getTaskById(taskId)
            withContext(Dispatchers.Main) {
                currentTask = task
                task?.let {
                    editTextTitle.setText(it.title)
                    spinnerPriority.setSelection(it.priority.ordinal)
                    selectedDueDate = it.dueDate
                    updateDueDateDisplay()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        buttonSave.setOnClickListener {
            saveTask()
        }
        
        buttonCancel.setOnClickListener {
            finish()
        }
        
        editTextDueDate.setOnClickListener {
            showDatePicker()
        }
        
        buttonClearDueDate.setOnClickListener {
            selectedDueDate = null
            updateDueDateDisplay()
        }
    }
    
    private fun saveTask() {
        val title = editTextTitle.text.toString().trim()
        
        if (title.isEmpty()) {
            Toast.makeText(this, "タイトルを入力してください", Toast.LENGTH_SHORT).show()
            return
        }
        
        val selectedPriority = Priority.values()[spinnerPriority.selectedItemPosition]
        
        currentTask?.let { task ->
            if (task.title != title) {
                viewModel.updateTask(taskId, title)
            }
            if (task.priority != selectedPriority) {
                viewModel.updateTaskPriority(taskId, selectedPriority)
            }
            if (task.dueDate != selectedDueDate) {
                viewModel.updateTaskDueDate(taskId, selectedDueDate)
            }
        }
        
        Toast.makeText(this, "タスクが更新されました", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDueDate?.let {
            calendar.time = it
        }
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                selectedDueDate = newCalendar.time
                updateDueDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun updateDueDateDisplay() {
        editTextDueDate.setText(
            selectedDueDate?.let { dateFormat.format(it) } ?: ""
        )
    }
}