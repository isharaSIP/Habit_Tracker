package com.example.habitzen.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.habitzen.LoginActivity
import com.example.habitzen.R
import com.example.habitzen.data.Prefs
import com.example.habitzen.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefs: Prefs
    private lateinit var currentUsername: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentUsername = sharedPreferences.getString("current_user", "Guest") ?: "Guest"
        prefs = Prefs(requireContext(), currentUsername)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayUserDetails()

        binding.editProfileButton.setOnClickListener { showEditProfileDialog() }
        binding.deleteAccountButton.setOnClickListener { showDeleteAccountConfirmation() }
        binding.logoutButton.setOnClickListener { logout() }
    }

    private fun displayUserDetails() {
        val password = sharedPreferences.getString(currentUsername, "")
        binding.profileUsername.text = "Username: $currentUsername"
        binding.profilePassword.text = "Password: ${ "*" .repeat(password?.length ?: 0)}"
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)
        val newUsernameInput = dialogView.findViewById<EditText>(R.id.edit_username_input)
        val newPasswordInput = dialogView.findViewById<EditText>(R.id.edit_password_input)

        // Pre-fill with current username if available
        newUsernameInput.setText(currentUsername)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val newUsername = newUsernameInput.text.toString().trim()
                val newPassword = newPasswordInput.text.toString().trim()

                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Username and password cannot be empty.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Check if new username already exists and is not the current user
                if (newUsername != currentUsername && sharedPreferences.contains(newUsername)) {
                    Toast.makeText(requireContext(), "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Update SharedPreferences
                val editor = sharedPreferences.edit()
                // Remove old username and password if username changed
                if (newUsername != currentUsername) {
                    editor.remove(currentUsername) // Remove old password entry
                    prefs.deleteUserSpecificData(currentUsername) // Clear old user's habit/mood data
                }
                editor.putString(newUsername, newPassword)
                editor.putString("current_user", newUsername)
                editor.apply()

                currentUsername = newUsername // Update current username
                prefs = Prefs(requireContext(), currentUsername) // Re-initialize prefs with new username
                displayUserDetails()
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteAccountConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone and all your data will be lost.")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteAccount()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteAccount() {
        // Clear user data from SharedPreferences
        val editor = sharedPreferences.edit()
        editor.remove(currentUsername) // Remove password entry
        editor.remove("current_user") // Clear current logged-in user
        editor.apply()

        prefs.deleteUserSpecificData(currentUsername) // Delete user-specific habit/mood data

        Toast.makeText(requireContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }

    private fun logout() {
        // Clear current logged-in user from SharedPreferences
        sharedPreferences.edit().remove("current_user").apply()
        Toast.makeText(requireContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
