
package com.example.habitzen.ui

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habitzen.data.Prefs
import com.example.habitzen.databinding.FragmentSettingsBinding
import com.example.habitzen.work.HydrationWorker

import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import android.widget.Toast
import androidx.work.ExistingPeriodicWorkPolicy

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val username = sharedPreferences.getString("current_user", "Guest") ?: "Guest"
        prefs = Prefs(requireContext(), username)

        loadReminderState()

        binding.startReminderBtn.setOnClickListener {
            val minutesString = binding.intervalInput.text.toString()
            val minutes = minutesString.toLongOrNull()

            if (minutes == null || minutes <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid interval in minutes (e.g., 30)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val actualMinutes = if (minutes < 15) {
                Toast.makeText(requireContext(), "Minimum reminder interval is 15 minutes. Setting to 15 minutes.", Toast.LENGTH_LONG).show()
                15L
            } else {
                minutes
            }

            val request = PeriodicWorkRequestBuilder<HydrationWorker>(actualMinutes, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "hydration_$username", ExistingPeriodicWorkPolicy.UPDATE, request
            )
            prefs.setReminderInterval(actualMinutes)
            updateReminderStatus(actualMinutes)
            Toast.makeText(requireContext(), "Hydration reminder set for every $actualMinutes minutes", Toast.LENGTH_SHORT).show()
        }

        binding.stopReminderBtn.setOnClickListener {
            WorkManager.getInstance(requireContext()).cancelUniqueWork("hydration_$username")
            prefs.clearReminderInterval()
            updateReminderStatus(0L)
            Toast.makeText(requireContext(), "Hydration reminder stopped", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadReminderState() {
        val interval = prefs.getReminderInterval()
        if (interval > 0) {
            binding.intervalInput.setText(interval.toString())
            updateReminderStatus(interval)
        } else {
            updateReminderStatus(0L)
        }
    }

    private fun updateReminderStatus(minutes: Long) {
        if (minutes > 0) {
            binding.reminderStatusText.text = "Reminder set for every $minutes minutes."
        } else {
            binding.reminderStatusText.text = "No reminder set."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
