
package com.example.habitzen.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.habitzen.data.Habit
import com.example.habitzen.data.Prefs
import com.example.habitzen.databinding.ItemHabitBinding
import android.view.View
import androidx.core.content.ContextCompat
import com.example.habitzen.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitsListAdapter(
    private val onToggle: (Habit, Boolean) -> Unit,
    private val onDelete: (Habit) -> Unit,
    private val onEdit: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsListAdapter.VH>() {

    private val items = mutableListOf<Habit>()
    private var completionMap: Map<String, Boolean> = emptyMap()

    fun submit(context: Context, prefs: Prefs) {
        items.clear()
        items.addAll(prefs.getHabits())
        completionMap = prefs.getTodayCompletionMap()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(private val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {
        private var listener: CompoundButton.OnCheckedChangeListener? = null
        fun bind(habit: Habit) {
            binding.habitTitle.text = habit.title

            // Format and display date and time
            val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.habitDateTime.text = dateTimeFormatter.format(Date(habit.dateTime))

            binding.habitDone.setOnCheckedChangeListener(null)
            binding.habitDone.isChecked = completionMap[habit.id] == true

            // Set background color based on checked state
            val backgroundColor = if (binding.habitDone.isChecked) {
                ContextCompat.getColor(binding.root.context, R.color.habit_background_checked)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.habit_background_unchecked)
            }
            binding.root.setCardBackgroundColor(backgroundColor)

            listener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                onToggle(habit, isChecked)
                val newBackgroundColor = if (isChecked) {
                    ContextCompat.getColor(binding.root.context, R.color.habit_background_checked)
                } else {
                    ContextCompat.getColor(binding.root.context, R.color.habit_background_unchecked)
                }
                binding.root.setCardBackgroundColor(newBackgroundColor)
            }
            binding.habitDone.setOnCheckedChangeListener(listener)
            binding.deleteBtn.setOnClickListener { onDelete(habit) }
            binding.editBtn.setOnClickListener { onEdit(habit) }
        }
    }
}
