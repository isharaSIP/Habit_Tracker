
package com.example.habitzen.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class Prefs(private val context: Context, private val username: String) {
    private val sp = context.getSharedPreferences("habit_prefs_$username", Context.MODE_PRIVATE)
    private val gson = Gson()

    //Retrieve all habits for current user
    fun getHabits(): MutableList<Habit> {
        val json = sp.getString("habits", "[]")
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        return gson.fromJson(json, type)
    }

    //unique identifier for each habit
    fun addHabit(title: String, dateTime: Long) {
        val list = getHabits()
        list.add(Habit(UUID.randomUUID().toString(), title, dateTime))
        sp.edit().putString("habits", gson.toJson(list)).apply()
    }

    //habit data update
    fun updateHabit(id: String, newTitle: String, newDateTime: Long) {
        val list = getHabits()
        val habitToUpdate = list.find { it.id == id }
        habitToUpdate?.let { habit ->
            habit.title = newTitle
            habit.dateTime = newDateTime
            sp.edit().putString("habits", gson.toJson(list)).apply()
        }
    }

    //delete habit data
    fun deleteHabit(id: String) {
        val list = getHabits().filter { it.id != id }
        sp.edit().putString("habits", gson.toJson(list)).apply()
        // Also clear completion flag for today
        val today = todayKey()
        val map = getTodayCompletionMap().toMutableMap()
        map.remove(id)
        sp.edit().putString(today, gson.toJson(map)).apply()
    }

    //Creates unique key for each day
    private fun todayKey(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
        return "today_" + sdf.format(Date())
    }

    fun getTodayCompletionMap(): Map<String, Boolean> {
        val key = todayKey()
        val json = sp.getString(key, "{}")
        val type = object : TypeToken<Map<String, Boolean>>() {}.type
        return gson.fromJson(json, type)
    }

    fun setHabitDoneToday(habitId: String, done: Boolean) {
        val key = todayKey()
        val map = getTodayCompletionMap().toMutableMap()
        map[habitId] = done
        sp.edit().putString(key, gson.toJson(map)).apply()
    }

    fun completionPercentToday(): Int {
        val habits = getHabits()
        if (habits.isEmpty()) return 0
        val map = getTodayCompletionMap()
        val done = habits.count { map[it.id] == true }
        return ((done.toDouble() / habits.size) * 100).toInt()
    }

    fun setReminderInterval(minutes: Long) {
        sp.edit().putLong("hydration_reminder_interval", minutes).apply()
    }

    fun getReminderInterval(): Long {
        return sp.getLong("hydration_reminder_interval", 0L)
    }

    fun clearReminderInterval() {
        sp.edit().remove("hydration_reminder_interval").apply()
    }

    fun addMood(entry: MoodEntry) {
        val list = getMoods()
        list.add(0, entry)
        sp.edit().putString("moods", gson.toJson(list)).apply()
    }

    fun getMoods(): MutableList<MoodEntry> {
        val json = sp.getString("moods", "[]")
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearAllMoods() {
        sp.edit().remove("moods").apply()
    }

    //Removes all data for specific user
    fun deleteUserSpecificData(usernameToDelete: String) {
        context.getSharedPreferences("habit_prefs_$usernameToDelete", Context.MODE_PRIVATE).edit().clear().apply()
    }
}
