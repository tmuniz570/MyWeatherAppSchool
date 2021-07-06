package com.tmuniz570.myweatherapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tmuniz570.myweatherapp.model.weather.Lista
import java.util.*
import kotlin.collections.ArrayList

class Adapter (private var list : MutableList<Lista> = ArrayList(), private var clickListener: OnClickListener, private var units: String?, private val arquivo: (String) -> Unit) : RecyclerView.Adapter<Adapter.HolderData>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val rHD = LayoutInflater.from(parent.context).inflate(R.layout.item_rview, parent, false)
        return HolderData(rHD)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val dados = list[position]

        holder.nome.text = dados.name
        holder.temp.text = dados.main.temp.toString() + units
        holder.id.text = "ID: " + dados.id

        val icon = dados.weather[0].icon
        val url = "http://openweathermap.org/img/wn/$icon@2x.png"
        Glide.with(holder.itemView).load(url).into(holder.icon)

        holder.initializeClick(list, clickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class HolderData(v: View) : RecyclerView.ViewHolder(v) {
        var nome = v.findViewById<TextView>(R.id.cityName)
        var temp = v.findViewById<TextView>(R.id.cityTemp)
        var icon = v.findViewById<ImageView>(R.id.iv_time_cloud)
        var id = v.findViewById<TextView>(R.id.id)

        fun initializeClick(item: MutableList<Lista>, action: OnClickListener) {

            itemView.setOnClickListener {
                action.onItemClick(item[position], adapterPosition)
            }
        }
    }

    fun get(lista: MutableList<Lista>) {
        list = lista
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onItemClick(item: Lista, position: Int) {

        }
    }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun swap(initPosition: Int, targetPosition: Int) {
        Collections.swap(list, initPosition, targetPosition)
        notifyItemMoved(initPosition, targetPosition)
    }

    fun favorite(pos: Int){
        arquivo(list[pos].id)
        notifyDataSetChanged()
    }
}