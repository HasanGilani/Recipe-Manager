package com.example.recipemanagerapp

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: Switch
    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        notificationSwitch = findViewById(R.id.notificationSwitch)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false)
        notificationSwitch.isChecked = notificationsEnabled

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
                    } else {
                        enableNotifications(editor)
                    }
                } else {
                    enableNotifications(editor)
                }
            } else {
                disableNotifications(editor)
            }
        }
    }

    private fun enableNotifications(editor: SharedPreferences.Editor) {
        editor.putBoolean("notifications_enabled", true)
        editor.apply()
        Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
    }

    private fun disableNotifications(editor: SharedPreferences.Editor) {
        editor.putBoolean("notifications_enabled", false)
        editor.apply()
        Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                enableNotifications(editor)
            } else {
                Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT).show()
                notificationSwitch.isChecked = false
            }
        }
    }
}