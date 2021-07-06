package com.tmuniz570.myweatherapp.ui.search

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
import com.tmuniz570.myweatherapp.databinding.FragmentSearchBinding
import com.tmuniz570.myweatherapp.model.RetrofitInitializer
import com.tmuniz570.myweatherapp.model.weather.Find
import com.tmuniz570.myweatherapp.model.weather.Lista
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SearchFragment : Fragment(), Adapter.OnClickListener {

    private var lista : MutableList<Lista> = ArrayList()
    private lateinit var tempUnits: String
    private val adapter by lazy { Adapter(lista, this, tempUnits, { id -> addFavorito(id) }) }

    private lateinit var units: String

    private lateinit var searchViewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        searchViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val sharedPref = context?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
        if (sharedPref != null){
            val local = sharedPref.getString("local", "")
            binding.etCity.setText(local)
            consulta(sharedPref, local.toString())
        }

        binding.btnSearch.setOnClickListener {
            if (context != null){
                if (!isInternetAvailable(requireContext())){
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }

            consulta(sharedPref, binding.etCity.text.toString())
        }
        return root
    }

    private fun consulta(sharedPref: SharedPreferences?, local: String){
        if (sharedPref != null){
            if (sharedPref.getBoolean("temp_C", true)){
                units = "metric"
                tempUnits = "ºC"
            } else{
                units = "imperial"
                tempUnits = "ºF"
            }
            with(sharedPref.edit()){
                putString("local", binding.etCity.text.toString())
                apply()
            }
        }

        val callFind = RetrofitInitializer().service().listFind(
            local,
            units,
            "c80b07d002800e502f16cf21b7a1f545")

        callFind.enqueue(object: Callback<Find> {
            override fun onResponse(call: Call<Find>?, response: Response<Find>?) {
                response?.body()?.let {
                    val result: Find = it
                    lista = result.list.toMutableList()
                    setup()
                    adapter.get(lista)
                }
            }

            override fun onFailure(call: Call<Find>?, t: Throwable?) {
                Log.d("Find", "Fail")
            }
        })
    }

    private fun setup() {
        binding.rvWeather.layoutManager = LinearLayoutManager(context)
        binding.rvWeather.setHasFixedSize(true)
        binding.rvWeather.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(SwipeDrag(adapter, lista))
        itemTouchHelper.attachToRecyclerView(binding.rvWeather)
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

    private fun addFavorito(id: String){
        val file = File(context?.filesDir, "ids.txt")
        file.createNewFile()

        writeContent(file, readContent(file) + "$id, ")
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}