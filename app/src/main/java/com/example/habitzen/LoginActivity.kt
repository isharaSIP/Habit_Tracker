package com.example.habitzen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.username_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            loginUser()
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun loginUser() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            return
        }

        val savedPassword = sharedPreferences.getString(username, null)

        if (savedPassword == password) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            with(sharedPreferences.edit()) {
                putString("current_user", username)
                apply()
            }
            // Redirect to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            return
        }

        if (sharedPreferences.contains(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
        } else {
            with(sharedPreferences.edit()) {
                putString(username, password)
                putString("current_user", username)
                apply()
            }
            Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show()
            // After successful registration, directly log them in and redirect
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
