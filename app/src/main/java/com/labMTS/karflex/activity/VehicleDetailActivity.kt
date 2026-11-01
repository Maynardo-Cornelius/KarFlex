package com.labMTS.karflex.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.labMTS.karflex.R
import com.labMTS.karflex.database.DatabaseHelper
import java.text.NumberFormat
import java.util.Locale

class VehicleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_detail)

        val ivVehicle = findViewById<ImageView>(R.id.ivVehicle)
        val tvBrand = findViewById<TextView>(R.id.tvBrand)
        val tvModelName = findViewById<TextView>(R.id.tvModelName)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val tvType = findViewById<TextView>(R.id.tvType)
        val tvTransmission = findViewById<TextView>(R.id.tvTransmission)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val tvRentedNotice = findViewById<TextView>(R.id.tvRentedNotice)
        val layoutRentForm = findViewById<LinearLayout>(R.id.layoutRentForm)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val btnRent = findViewById<Button>(R.id.btnRent)

        val db = DatabaseHelper(this)

        val vehicleId = intent.getIntExtra("vehicleId", -1)
        val vehicle = db.getVehicleById(vehicleId)
        if(vehicle == null){
            Toast.makeText(this, "Vehicle not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvBrand.text = vehicle.brand
        tvModelName.text = vehicle.modelName
        tvType.text = vehicle.type
        tvTransmission.text = vehicle.transmission
        tvDescription.text = vehicle.description

        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
        tvPrice.text = getString(R.string.price_format, formatter.format(vehicle.pricePerDay))

        Glide.with(this).load(vehicle.image).placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).into(ivVehicle)

        db.updateExpiredRentals()
        if(db.isVehicleRented(vehicleId)) {
            layoutRentForm.visibility = View.GONE
            tvRentedNotice.visibility = View.VISIBLE
        } else {
            layoutRentForm.visibility = View.VISIBLE
            tvRentedNotice.visibility = View.GONE
        }

        btnRent.setOnClickListener {
            val durationText = etDuration.text.toString().trim()

            if (durationText.isEmpty()) {
                Toast.makeText(this, "Duration must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = durationText.toIntOrNull()
            if(duration == null) {
                Toast.makeText(this, "Duration must be a number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(duration <= 0 ){
                Toast.makeText(this, "Duration must be more than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("KarFlexPrefs", MODE_PRIVATE)
            val userId = prefs.getInt("userId", -1)
            if(userId == -1){
                Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(db.insertRental(vehicleId, userId, duration)) {
                Toast.makeText(this, "Rental successful", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Rental failed", Toast.LENGTH_SHORT).show()
            }
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}