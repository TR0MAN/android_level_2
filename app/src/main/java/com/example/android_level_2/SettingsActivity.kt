package com.example.android_level_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.android_level_2.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMyProfileViewContact.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnCustomGoogleButton.setOnClickListener {
            Toast.makeText(this, getString(R.string.custom_google_button_text), Toast.LENGTH_SHORT).show()
        }

    }
}