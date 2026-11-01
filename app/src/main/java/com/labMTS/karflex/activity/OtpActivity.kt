package com.labMTS.karflex.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.labMTS.karflex.R
import androidx.core.content.edit

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp)

        val etOtp = findViewById<EditText>(R.id.etOtp)
        val btnVerify = findViewById<Button>(R.id.btnVerify)

        val correctOtp = intent.getStringExtra("otp")
        val userId = intent.getIntExtra("userId", -1)

        btnVerify.setOnClickListener {
            val inputOtp = etOtp.text.toString().trim()

            if (inputOtp.isEmpty()) {
                Toast.makeText(this, "OTP code must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputOtp != correctOtp) {
                Toast.makeText(this, "Invalid OTP code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("KarFlexPrefs", MODE_PRIVATE)
            prefs.edit { putInt("userId", userId) }

            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}