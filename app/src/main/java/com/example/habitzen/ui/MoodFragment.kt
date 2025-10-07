
package com.example.habitzen.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habitzen.data.MoodEntry
import com.example.habitzen.data.Prefs
import com.example.habitzen.databinding.FragmentMoodBinding
import android.app.AlertDialog
import android.widget.Toast
import android.content.Intent

class MoodFragment : Fragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs
    private lateinit var adapter: MoodListAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedEmoji = "😊"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val username = sharedPreferences.getString("current_user", "Guest") ?: "Guest"
        prefs = Prefs(requireContext(), username)
        adapter = MoodListAdapter()
        binding.moodsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.moodsRecycler.adapter = adapter

        binding.emojiHappy.setOnClickListener { selectedEmoji = "😊" }
        binding.emojiNeutral.setOnClickListener { selectedEmoji = "😐" }
        binding.emojiSad.setOnClickListener { selectedEmoji = "😢" }
        binding.emojiAngry.setOnClickListener { selectedEmoji = "😠" }

        binding.logMoodBtn.setOnClickListener {
            prefs.addMood(MoodEntry(System.currentTimeMillis(), selectedEmoji, binding.moodNote.text.toString().ifBlank { null }))
            binding.moodNote.text?.clear()
            refresh()
        }

        binding.clearMoodsBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Moods")
                .setMessage("Are you sure you want to delete all your logged moods? This action cannot be undone.")
                .setPositiveButton("Clear") { dialog, _ ->
                    prefs.clearAllMoods()
                    refresh()
                    Toast.makeText(requireContext(), "All moods cleared.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.shareSummaryBtn.setOnClickListener {
            // Build a simple last-7-days mood summary
            val moods = prefs.getMoods().take(50)
            val top = moods.groupBy { it.emoji }.mapValues { it.value.size }
            val summary = if (top.isEmpty()) "No moods logged yet." else top.entries.joinToString { "${it.key}: ${it.value}" }
            val text = "My Mood Summary (recent): $summary"
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(send, "Share via"))
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        adapter.submitList(prefs.getMoods())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
