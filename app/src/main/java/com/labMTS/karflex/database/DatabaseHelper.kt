package com.labMTS.karflex.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.apply

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "KarFlex.db"
        const val DATABASE_VERSION = 1
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Users (
            userId INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            phoneNumber TEXT NOT NULL
        )
        """)
        db.execSQL("""
            CREATE TABLE Vehicle (
            vehicleId INTEGER PRIMARY KEY,
            modelName TEXT NOT NULL,
            type TEXT NOT NULL,
            brand TEXT NOT NULL,
            pricePerDay INTEGER NOT NULL,
            transmission TEXT NOT NULL,
            image TEXT NOT NULL,
            description TEXT NOT NULL
        )
        """)
        db.execSQL("""
            CREATE TABLE Rentals (
            rentalId INTEGER PRIMARY KEY AUTOINCREMENT,
            vehicleId INTEGER NOT NULL,
            userId INTEGER NOT NULL,
            duration INTEGER NOT NULL,
            startDate TEXT NOT NULL,
            status TEXT NOT NULL,
            FOREIGN KEY (vehicleId) REFERENCES Vehicle(vehicleId),
            FOREIGN KEY (userId) REFERENCES Users(userId)
        )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS Rentals")
        db.execSQL("DROP TABLE IF EXISTS Users")
        db.execSQL("DROP TABLE IF EXISTS Vehicle")
        onCreate(db)
    }

    //Users
    fun insertUser(username: String, password: String, phone: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("phoneNumber", phone)
        }
        val result = db.insert("Users", null, values)
        db.close()
        return result != -1L
    }

    fun login(username: String, password: String): Users? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )
        var user: Users? = null
        if(cursor.moveToFirst()) {
            user = Users(
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun getUserById(userId: Int): Users? {
        val db = readableDatabase
        var user: Users? = null
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE userId = ?",
            arrayOf(userId.toString())
        )
        if(cursor.moveToFirst()) {
            user = Users (
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE username = ?",
            arrayOf(username)
        )
        val usernameExists = cursor.count > 0
        cursor.close()
        db.close()
        return usernameExists
    }

    //Vehicles
    fun insertVehicle(vehicle: Vehicle): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("vehicleId", vehicle.vehicleId)
            put("modelName", vehicle.modelName)
            put("type", vehicle.type)
            put("brand", vehicle.brand)
            put("pricePerDay", vehicle.pricePerDay)
            put("transmission", vehicle.transmission)
            put("image", vehicle.image)
            put("description", vehicle.description)
        }
        val result = db.insertWithOnConflict("Vehicle", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close()
        return result != -1L
    }

    fun getAllVehicles(): List<Vehicle> {
        val db = readableDatabase
        val list = ArrayList<Vehicle>()
        val cursor = db.rawQuery(
            "SELECT * FROM Vehicle", null
        )
        while(cursor.moveToNext()) {
            val vehicle = Vehicle(
                cursor.getInt(cursor.getColumnIndexOrThrow("vehicleId")),
                cursor.getString(cursor.getColumnIndexOrThrow("modelName")),
                cursor.getString(cursor.getColumnIndexOrThrow("type")),
                cursor.getString(cursor.getColumnIndexOrThrow("brand")),
                cursor.getInt(cursor.getColumnIndexOrThrow("pricePerDay")),
                cursor.getString(cursor.getColumnIndexOrThrow("transmission")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getString(cursor.getColumnIndexOrThrow("description"))
            )
            list.add(vehicle)
        }
        cursor.close()
        db.close()
        return list
    }

    fun getVehicleById(vehicleId: Int): Vehicle? {
        val db = readableDatabase
        var vehicle: Vehicle? = null
        val cursor = db.rawQuery(
            "SELECT * FROM Vehicle WHERE vehicleId = ?", arrayOf(vehicleId.toString())
        )
        if(cursor.moveToFirst()) {
            vehicle = Vehicle (
                cursor.getInt(cursor.getColumnIndexOrThrow("vehicleId")),
                cursor.getString(cursor.getColumnIndexOrThrow("modelName")),
                cursor.getString(cursor.getColumnIndexOrThrow("type")),
                cursor.getString(cursor.getColumnIndexOrThrow("brand")),
                cursor.getInt(cursor.getColumnIndexOrThrow("pricePerDay")),
                cursor.getString(cursor.getColumnIndexOrThrow("transmission")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getString(cursor.getColumnIndexOrThrow("description"))
            )
        }
        cursor.close()
        db.close()
        return vehicle
    }

    fun isVehicleRented(vehicleId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Rentals WHERE vehicleId = ? AND status = ?",
            arrayOf(vehicleId.toString(), "Active")
        )
        val isRented = cursor.count > 0
        cursor.close()
        db.close()
        return isRented
    }

    //Rentals
    fun insertRental(vehicleId: Int, userId: Int, duration: Int): Boolean {
        val db = writableDatabase
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put("vehicleId", vehicleId)
            put("userId", userId)
            put("duration", duration)
            put("startDate", today)
            put("status", "Active")
        }
        val result = db.insert("Rentals", null, values)
        db.close()
        return result != -1L
    }

    fun getActiveRentals(userId: Int): List<Rentals> {
        val db = readableDatabase
        val list = ArrayList<Rentals>()
        val cursor = db.rawQuery(
            "SELECT * FROM Rentals WHERE userId = ? AND status = ?",
            arrayOf(userId.toString(), "Active")
        )
        while(cursor.moveToNext()) {
            val rental = Rentals(
                cursor.getInt(cursor.getColumnIndexOrThrow("rentalId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("vehicleId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("duration")),
                cursor.getString(cursor.getColumnIndexOrThrow("startDate")),
                cursor.getString(cursor.getColumnIndexOrThrow("status"))
            )
            list.add(rental)
        }
        cursor.close()
        db.close()
        return list
    }

    fun getRentalHistory(userId: Int): List<Rentals> {
        val db = readableDatabase
        val list = ArrayList<Rentals>()
        val cursor = db.rawQuery(
            "SELECT * FROM Rentals WHERE userId = ? ORDER BY rentalId DESC",
            arrayOf(userId.toString())
        )
        while(cursor.moveToNext()) {
            val rental = Rentals(
                cursor.getInt(cursor.getColumnIndexOrThrow("rentalId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("vehicleId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("duration")),
                cursor.getString(cursor.getColumnIndexOrThrow("startDate")),
                cursor.getString(cursor.getColumnIndexOrThrow("status"))
            )
            list.add(rental)
        }
        cursor.close()
        db.close()
        return list
    }

    fun updateRentalDuration(rentalId: Int, newDuration: Int): Boolean{
        val db = writableDatabase
        val values = ContentValues().apply {
            put("duration", newDuration)
        }
        val result = db.update(
            "Rentals",
            values,
            "rentalId = ?",
            arrayOf(rentalId.toString())
        )
        db.close()
        return result > 0
    }

    fun deleteRental(rentalId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(
            "Rentals",
            "rentalId = ?",
            arrayOf(rentalId.toString())
        )
        db.close()
        return result > 0
    }

    fun updateExpiredRentals() {
        val db = writableDatabase
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = format.format(Date())
        val cursor = db.rawQuery(
            "SELECT * FROM Rentals WHERE status = ?",
            arrayOf("Active")
        )
        while (cursor.moveToNext()) {
            val rentalId = cursor.getInt(cursor.getColumnIndexOrThrow("rentalId"))
            val startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))

            val start = format.parse(startDate)
            val calendar = Calendar.getInstance()
            calendar.time =start
            calendar.add(Calendar.DAY_OF_MONTH, duration)
            val endDate = format.format(calendar.time)
            if (today > endDate) {
                db.update(
                    "Rentals",
                    ContentValues().apply{put("status", "Completed")},
                    "rentalId = ?",
                    arrayOf(rentalId.toString())
                )
            }
        }
        cursor.close()
        db.close()
    }
}
