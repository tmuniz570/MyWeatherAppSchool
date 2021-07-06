package com.tmuniz570.myweatherapp.ui.faforite

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmuniz570.myweatherapp.Adapter
import com.tmuniz570.myweatherapp.R
import com.tmuniz570.myweatherapp.SwipeDrag
import com.tmuniz570.myweatherapp.databinding.FragmentFavoriteBinding
import com.tmuniz570.myweatherapp.model.RetrofitInitializer
import com.tmuniz570.myweatherapp.model.weather.Lista
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FavoriteFragment : Fragment(), Adapter.OnClickListener {

    private var lista : MutableList<Lista> = ArrayList()
    private lateinit var tempUnits: String
    private val adapter by lazy { Adapter(lista, this, tempUnits, { id -> delFavorito(id)}) }

    private lateinit var units: String

    private lateinit var favoriteViewModel: FavoriteViewModel
    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favoriteViewModel =
            ViewModelProvider(this).get(FavoriteViewModel::class.java)

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        favoriteViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        if (context != null){
            if (!isInternetAvailable(requireContext())){
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        consulta()

        return root
    }

    private fun consulta(){
        val sharedPref = context?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
        if (sharedPref != null){
            if (sharedPref.getBoolean("temp_C", true)){
                units = "metric"
                tempUnits = "ºC"
            } else{
                units = "imperial"
                tempUnits = "ºF"
            }
        }

        val room = lerArquivo()
        Log.d("TAG", room.toString())

        setup()
        lista.clear()
        for (idCity in room){
            val callWeather = RetrofitInitializer().service().listWeather(
                idCity,
                units,
                "c80b07d002800e502f16cf21b7a1f545")

            callWeather.enqueue(object: Callback<Lista> {
                override fun onResponse(call: Call<Lista>?, response: Response<Lista>?) {
                    response?.body()?.let {
                        val result: Lista = it
                        lista.add(result)
                        adapter.get(lista)
                    }
                }

                override fun onFailure(call: Call<Lista>?, t: Throwable?) {
                    Log.d("Weather", "Fail")
                }
            })
        }
    }

    private fun setup() {
        binding.rvFavorite.layoutManager = LinearLayoutManager(context)
        binding.rvFavorite.setHasFixedSize(true)
        binding.rvFavorite.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(SwipeDrag(adapter, lista))
        itemTouchHelper.attachToRecyclerView(binding.rvFavorite)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        return result
    }

    private fun lerArquivo(): Set<String>{
        val file = File(context?.filesDir, "ids.txt")
        file.createNewFile()
        return readContent(file).split(", ").toSet()
    }

    private fun readContent(file: File): String {
        file.inputStream().use {
            return it.readBytes().decodeToString()
        }
    }
    private fun writeContent(file: File, write: String){
        file.outputStream().use {
            it.write(write.toByteArray())
        }
    }

    private fun delFavorito(id: String){
        val file = File(context?.filesDir, "ids.txt")
        file.createNewFile()
        var ids = lerArquivo().toMutableList()
        ids.remove(id)
        if (ids.size == 1){
            writeContent(file, "")
        } else {
            writeContent(file, ids.toString().substring(1, ids.toString().length-1))
        }
        consulta()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}