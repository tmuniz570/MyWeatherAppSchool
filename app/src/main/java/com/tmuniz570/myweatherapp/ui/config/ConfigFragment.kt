package com.tmuniz570.myweatherapp.ui.config

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tmuniz570.myweatherapp.R
import com.tmuniz570.myweatherapp.databinding.FragmentConfigBinding

class ConfigFragment : Fragment() {

    private lateinit var configViewModel: ConfigViewModel
    private var _binding: FragmentConfigBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configViewModel =
            ViewModelProvider(this).get(ConfigViewModel::class.java)

        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        configViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val sharedPref = context?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)

        if (sharedPref != null){
            val langPT = sharedPref.getBoolean("lang_PT", true)
            val tempC = sharedPref.getBoolean("temp_C", true)

            if (langPT){
                binding.rbPt.isChecked = true
            } else{
                binding.rbEn.isChecked = true
            }

            if (tempC){
                binding.rbC.isChecked = true
            } else{
                binding.rbF.isChecked = true
            }
        }

        binding.btnSave.setOnClickListener {
            if (sharedPref != null){
                with(sharedPref.edit()){
                    putBoolean("lang_PT", binding.rbPt.isChecked)
                    putBoolean("temp_C", binding.rbC.isChecked)
                    apply()
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}