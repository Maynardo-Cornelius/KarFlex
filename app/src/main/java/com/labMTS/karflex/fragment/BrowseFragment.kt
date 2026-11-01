package com.labMTS.karflex.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.labMTS.karflex.R
import com.labMTS.karflex.activity.VehicleDetailActivity
import com.labMTS.karflex.adapter.VehicleAdapter
import com.labMTS.karflex.database.DatabaseHelper
import com.labMTS.karflex.database.Vehicle


class BrowseFragment : Fragment() {

    private val jsonUrl = "https://gist.githubusercontent.com/axelkrnwn/01c506ac60735f553af951e550634b59/raw/08489b2ff76e1356a6df38d09948716628024195/karflex.json"
    private lateinit var rvVehicles: RecyclerView
    private lateinit var db: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        rvVehicles = view.findViewById(R.id.rvVehicles)
        rvVehicles.layoutManager = LinearLayoutManager(requireContext())

        fetchVehicles()
    }

    override fun onResume() {
        super.onResume()
        showVehicles()
    }

    private fun fetchVehicles() {
        val request = JsonArrayRequest(
            Request.Method.GET, jsonUrl, null,
            { response ->
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val vehicle = Vehicle(
                        vehicleId = obj.getInt("id"),
                        modelName = obj.getString("model_name"),
                        type = obj.getString("type"),
                        brand = obj.getString("brand"),
                        pricePerDay = obj.getInt("price_per_day"),
                        transmission = obj.getString("transmission"),
                        image = obj.getString("image_url"),
                        description = obj.getString("description")
                    )
                    db.insertVehicle(vehicle)
                }
                if (isAdded) showVehicles()
            },
            { error ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to load vehicles", Toast.LENGTH_SHORT).show()
                }
            }
        )
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun showVehicles() {
        val vehicles = db.getAllVehicles()
        rvVehicles.adapter = VehicleAdapter(vehicles) { vehicle ->
            val intent = Intent(requireContext(), VehicleDetailActivity::class.java)
            intent.putExtra("vehicleId", vehicle.vehicleId)
            startActivity(intent)
        }
    }
}