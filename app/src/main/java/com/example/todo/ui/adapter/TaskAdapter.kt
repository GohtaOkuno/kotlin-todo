package com.example.todo.ui.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.example.todo.R
import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val onTaskToggle: (Int) -> Unit,
    private val onTaskDelete: (Int) -> Unit,
    private val onTaskEdit: (Int, String) -> Unit,
    private val onTaskClick: (Int) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {
    
    private var shouldAnimateNewItem = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
        
        // 新しいアイテムが追加された場合のアニメーション（最初のアイテムのみ）
        if (shouldAnimateNewItem && position == 0) {
            animateItemAdd(holder.itemView)
            shouldAnimateNewItem = false
        }
    }
    
    override fun onCurrentListChanged(
        previousList: MutableList<Task>,
        currentList: MutableList<Task>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        // 新しいアイテムが追加された場合のみアニメーション
        if (currentList.size > previousList.size) {
            shouldAnimateNewItem = true
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBoxDone: CheckBox = itemView.findViewById(R.id.checkBoxDone)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewCreatedAt: TextView = itemView.findViewById(R.id.textViewCreatedAt)
        private val textViewPriority: TextView = itemView.findViewById(R.id.textViewPriority)
        private val textViewDueDate: TextView = itemView.findViewById(R.id.textViewDueDate)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
        
        private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        private val dueDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        fun bind(task: Task) {
            textViewTitle.text = task.title
            checkBoxDone.isChecked = task.isDone
            textViewCreatedAt.text = dateFormat.format(task.createdAt)
            
            // 優先度の表示設定
            textViewPriority.text = task.priority.displayName
            val priorityColor = when (task.priority) {
                Priority.HIGH -> ContextCompat.getColor(itemView.context, android.R.color.holo_red_light)
                Priority.NORMAL -> ContextCompat.getColor(itemView.context, android.R.color.holo_blue_light)
                Priority.LOW -> ContextCompat.getColor(itemView.context, android.R.color.holo_green_light)
            }
            textViewPriority.background.setTint(priorityColor)
            
            // 締切日の表示設定
            setupDueDateDisplay(task)
            
            // Apply strikethrough for completed tasks
            if (task.isDone) {
                textViewTitle.paintFlags = textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textViewTitle.paintFlags = textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            checkBoxDone.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != task.isDone) {
                    animateTaskToggle(textViewTitle, isChecked)
                    onTaskToggle(task.id)
                }
            }
            
            buttonDelete.setOnClickListener {
                animateItemDelete(itemView) {
                    onTaskDelete(task.id)
                }
            }
            
            itemView.setOnClickListener {
                onTaskClick(task.id)
            }
            
            textViewTitle.setOnClickListener {
                onTaskEdit(task.id, task.title)
            }
        }
        
        private fun setupDueDateDisplay(task: Task) {
            task.dueDate?.let { dueDate ->
                textViewDueDate.visibility = View.VISIBLE
                val dueDateText = "締切: ${dueDateFormat.format(dueDate)}"
                textViewDueDate.text = dueDateText
                
                // 期限の状態に応じて色を変更
                val now = Date()
                val calendar = Calendar.getInstance()
                calendar.time = now
                calendar.add(Calendar.DAY_OF_YEAR, 3) // 3日後
                val threeDaysLater = calendar.time
                
                when {
                    dueDate.before(now) -> {
                        // 期限切れ - 赤色
                        textViewDueDate.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                        textViewDueDate.text = "期限切れ: ${dueDateFormat.format(dueDate)}"
                    }
                    dueDate.before(threeDaysLater) -> {
                        // 期限が近い（3日以内） - オレンジ色
                        textViewDueDate.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark))
                    }
                    else -> {
                        // 通常 - グレー色
                        textViewDueDate.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                    }
                }
            } ?: run {
                textViewDueDate.visibility = View.GONE
            }
        }
    }
    
    // アニメーション関数
    private fun animateItemAdd(view: View) {
        view.alpha = 0f
        view.scaleX = 0.5f
        view.scaleY = 0.5f
        view.translationX = view.width.toFloat()
        
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationX(0f)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator())
            .start()
    }
    
    private fun animateItemDelete(view: View, onComplete: () -> Unit) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, view.width.toFloat())
        animator.duration = 250
        animator.interpolator = AccelerateDecelerateInterpolator()
        
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        fadeOut.duration = 250
        
        animator.doOnEnd {
            onComplete()
        }
        
        animator.start()
        fadeOut.start()
    }
    
    private fun animateTaskToggle(textView: TextView, isCompleted: Boolean) {
        // スケールアニメーション
        val scaleDown = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 0.9f)
        scaleDown.duration = 100
        
        val scaleUp = ObjectAnimator.ofFloat(textView, "scaleX", 0.9f, 1f)
        scaleUp.duration = 100
        scaleUp.interpolator = BounceInterpolator()
        
        scaleDown.doOnEnd {
            // テキストの見た目を更新
            if (isCompleted) {
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                // 完了時の追加アニメーション
                val bounceY = ObjectAnimator.ofFloat(textView, "translationY", 0f, -20f, 0f)
                bounceY.duration = 400
                bounceY.interpolator = BounceInterpolator()
                bounceY.start()
            } else {
                textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            scaleUp.start()
        }
        
        scaleDown.start()
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}