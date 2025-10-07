
package com.example.habitzen.data

data class Habit(val id: String, var title: String, var dateTime: Long = System.currentTimeMillis())
data class MoodEntry(val timestamp: Long, val emoji: String, val note: String?)
