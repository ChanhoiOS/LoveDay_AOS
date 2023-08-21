package com.chanho.loveday

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.chanho.loveday.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initNavigation()
        initSharedPreference()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.navBar, findNavController(R.id.nav_host))
    }

    private fun initSharedPreference() {
        preferences = getSharedPreferences("setDDay", MODE_PRIVATE);

        val isSet = preferences?.getBoolean("isSet", false)

        if (isSet == true) {
            Log.d("pre1:: ", isSet.toString())
        } else {
            Log.d("pre2:: ", isSet.toString())
            val intent = Intent(this, SetDayActivity::class.java)
            startActivity(intent)
        }

//        val editor  : SharedPreferences.Editor? = preferences?.edit();
//        editor?.putString("hello","안녕하세요")
//        editor?.commit() // data 저장!
    }
}