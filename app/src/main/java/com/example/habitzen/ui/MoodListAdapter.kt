
package com.example.habitzen.ui

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habitzen.data.MoodEntry
import com.example.habitzen.databinding.ItemMoodBinding
import java.util.Date

class MoodListAdapter : RecyclerView.Adapter<MoodListAdapter.VH>() {
    private val items = mutableListOf<MoodEntry>()

    fun submitList(data: List<MoodEntry>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(private val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MoodEntry) {
            val date = Date(item.timestamp)
            binding.moodLine.text = "${item.emoji}  •  " + DateFormat.format("MMM d, h:mm a", date)
            binding.moodNoteText.text = item.note ?: ""
        }
    }
}
