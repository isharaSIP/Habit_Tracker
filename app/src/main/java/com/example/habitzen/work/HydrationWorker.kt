
package com.example.habitzen.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.habitzen.R

class HydrationWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("current_user", "")
        
        val notificationTitle = if (username.isNullOrEmpty()) {
            "Hydration Reminder"
        } else {
            "Hey $username, Hydration Reminder!"
        }

        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "hydration"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(channelId, "Hydration", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val n = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificationTitle)
            .setContentText("Time to drink some water!")
            .setAutoCancel(true)
            .build()
        nm.notify(1001, n)
        return Result.success()
    }
}
