
package com.example.habitzen.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.example.habitzen.R
import com.example.habitzen.data.Prefs

class HabitWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        update(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, HabitWidget::class.java))
            update(context, manager, ids)
        }

        private fun update(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val username = sharedPreferences.getString("current_user", "Guest") ?: "Guest"
            val percent = Prefs(context, username).completionPercentToday()
            for (id in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_habit)
                views.setTextViewText(R.id.widgetPercent, "${percent}%")
                appWidgetManager.updateAppWidget(id, views)
            }
        }
    }
}
