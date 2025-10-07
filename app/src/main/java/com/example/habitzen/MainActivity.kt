
package com.example.habitzen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.habitzen.databinding.ActivityMainBinding
import com.example.habitzen.ui.HabitsFragment
import com.example.habitzen.ui.MoodFragment
import com.example.habitzen.ui.SettingsFragment
import com.example.habitzen.ui.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HabitsFragment())
                .commit()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> supportFragmentManager.beginTransaction().replace(R.id.container, HabitsFragment()).commit()
                R.id.nav_mood -> supportFragmentManager.beginTransaction().replace(R.id.container, MoodFragment()).commit()
                R.id.nav_profile -> supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment()).commit()
                R.id.nav_settings -> supportFragmentManager.beginTransaction().replace(R.id.container, SettingsFragment()).commit()
            }
            true
        }

        requestPostNotifications()
    }

    private fun requestPostNotifications() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
    }
}
