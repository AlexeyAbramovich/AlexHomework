package com.abra.homework_9.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abra.homework_9.database.CityData
import com.abra.homework_9.databinding.ItemCityNameBinding
import com.abra.homework_9.repositories.SharedPrefRepository

class CityDataAdapter : RecyclerView.Adapter<CityDataAdapter.CityDataViewHolder>() {
    private var citiesList = arrayListOf<CityData>()
    private var lastCheckedItem: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityDataViewHolder {
        val binding = ItemCityNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityDataViewHolder, position: Int) {
        holder.bind(citiesList[position])
    }

    override fun getItemCount() = citiesList.size

    inner class CityDataViewHolder(private val binding: ItemCityNameBinding) :
            RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cityData: CityData) {
            with(binding) {
                if (adapterPosition != lastCheckedItem) {
                    ivCheck.visibility = View.INVISIBLE
                }
                tvCityName1.text = "${cityData.name}, ${cityData.country}"
                root.setOnClickListener {
                    lastCheckedItem = adapterPosition
                    SharedPrefRepository(root.context).writeId(cityData.id)
                    ivCheck.visibility = View.VISIBLE
                    this@CityDataAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun updateList(list: List<CityData>) {
        citiesList = ArrayList(list)
        notifyDataSetChanged()
    }
}