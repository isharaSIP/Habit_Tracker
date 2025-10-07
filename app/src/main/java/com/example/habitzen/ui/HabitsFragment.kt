
package com.example.habitzen.ui

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habitzen.R
import com.example.habitzen.data.Prefs
import com.example.habitzen.databinding.FragmentHabitsBinding
import com.example.habitzen.widget.HabitWidget
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.TextView
import java.util.Calendar
import com.example.habitzen.data.Habit
import java.util.Date
import java.util.Locale

class HabitsFragment : Fragment() {
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs
    private lateinit var adapterHabits: HabitsListAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val username = sharedPreferences.getString("current_user", "Guest") ?: "Guest"
        binding.greetingTextView.text = "Hey $username, Welcome to HabitZen!"
        prefs = Prefs(requireContext(), username)
        adapterHabits = HabitsListAdapter(
            onToggle = { habit, done ->
                // Save state, refresh UI header + list, and update widget
                prefs.setHabitDoneToday(habit.id, done)
                refresh() // rebind list + updateCompletion()
                HabitWidget.updateAll(requireContext())
            },
            onDelete = { habit ->
                prefs.deleteHabit(habit.id)
                refresh()
                HabitWidget.updateAll(requireContext())
            },
            onEdit = { habit ->
                showEditDialog(habit)
            }
        )
        binding.habitsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.habitsRecycler.adapter = adapterHabits
        binding.addHabitFab.setOnClickListener { showAddDialog() }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        adapterHabits.submit(requireContext(), prefs)
        updateCompletion()
    }

    private fun updateCompletion() {
        // Update the header showing today's completion percent and progress bar.
        val total = prefs.getHabits().size
        val percent = prefs.completionPercentToday()
        val done = prefs.getTodayCompletionMap().count { it.value }
        // Guard against missing views if layout hasn't been created yet
        binding.completionText?.let {
            it.text = "Today: ${percent}% (${done}/${total})"
        }
        binding.completionPercentLabel?.let {
            it.text = "${percent}%"
        }
        binding.completionProgress?.let {
            it.progress = percent
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.new_habit_title_input)
        val datePickerText = dialogView.findViewById<TextView>(R.id.new_habit_date_picker_text)
        val timePickerText = dialogView.findViewById<TextView>(R.id.new_habit_time_picker_text)

        val calendar = Calendar.getInstance()
        var selectedDateTime: Long = calendar.timeInMillis

        // Set initial date and time text
        val dateFormatter = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
        datePickerText.text = dateFormatter.format(Date(selectedDateTime))
        timePickerText.text = timeFormatter.format(Date(selectedDateTime))

        datePickerText.setOnClickListener { _ ->
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDateTime = calendar.timeInMillis
                datePickerText.text = dateFormatter.format(Date(selectedDateTime))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        timePickerText.setOnClickListener { _ ->
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedDateTime = calendar.timeInMillis
                timePickerText.text = timeFormatter.format(Date(selectedDateTime))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { d, _ ->
                val title = titleInput.text.toString().trim()
                if (title.isNotEmpty()) {
                    prefs.addHabit(title, selectedDateTime)
                    refresh()
                    HabitWidget.updateAll(requireContext())
                }
                d.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { d, _ -> d.dismiss() }
            .show()
    }

    private fun showEditDialog(habit: com.example.habitzen.data.Habit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.new_habit_title_input)
        val datePickerText = dialogView.findViewById<TextView>(R.id.new_habit_date_picker_text)
        val timePickerText = dialogView.findViewById<TextView>(R.id.new_habit_time_picker_text)

        titleInput.setText(habit.title) // Pre-fill with existing habit title

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = habit.dateTime // Set calendar to existing habit's date/time
        var selectedDateTime: Long = habit.dateTime

        val dateFormatter = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
        datePickerText.text = dateFormatter.format(Date(selectedDateTime))
        timePickerText.text = timeFormatter.format(Date(selectedDateTime))

        datePickerText.setOnClickListener { _ ->
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDateTime = calendar.timeInMillis
                datePickerText.text = dateFormatter.format(Date(selectedDateTime))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        timePickerText.setOnClickListener { _ ->
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedDateTime = calendar.timeInMillis
                timePickerText.text = timeFormatter.format(Date(selectedDateTime))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { d, _ ->
                val newTitle = titleInput.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    prefs.updateHabit(habit.id, newTitle, selectedDateTime)
                    refresh()
                    HabitWidget.updateAll(requireContext())
                }
                d.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { d, _ -> d.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
