package com.labMTS.karflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.labMTS.karflex.R
import com.labMTS.karflex.database.DatabaseHelper
import com.labMTS.karflex.database.Vehicle
import java.text.NumberFormat
import java.util.Locale

class VehicleAdapter(
    private val vehicles: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardVehicle: MaterialCardView = itemView.findViewById(R.id.cardVehicle)
        val ivVehicle: ImageView = itemView.findViewById(R.id.ivVehicle)
        val tvBrand: TextView = itemView.findViewById(R.id.tvBrand)
        val tvModelName: TextView = itemView.findViewById(R.id.tvModelName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvTransmission: TextView = itemView.findViewById(R.id.tvTransmission)
        val tvRentedBadge: TextView = itemView.findViewById(R.id.tvRentedBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun getItemCount(): Int = vehicles.size

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicles[position]
        val context = holder.itemView.context

        holder.tvBrand.text = vehicle.brand
        holder.tvModelName.text = vehicle.modelName
        holder.tvTransmission.text = vehicle.transmission

        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
        holder.tvPrice.text = "Rp ${formatter.format(vehicle.pricePerDay)}"

        Glide.with(context)
            .load(vehicle.image)
            .placeholder(R.drawable.ic_logo)
            .error(R.drawable.ic_logo)
            .into(holder.ivVehicle)

        val isRented = DatabaseHelper(context).isVehicleRented(vehicle.vehicleId)
        if (isRented) {
            holder.cardVehicle.setCardBackgroundColor(ContextCompat.getColor(context, R.color.rented_red))
            holder.tvRentedBadge.visibility = View.VISIBLE
        } else {
            holder.cardVehicle.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_bg))
            holder.tvRentedBadge.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(vehicle)
        }
    }
}