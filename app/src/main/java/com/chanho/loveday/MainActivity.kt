package com.chanho.loveday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.chanho.loveday.application.MyApplication
import com.chanho.loveday.databinding.ActivityMainBinding
import com.chanho.loveday.noti.MyFirebaseMessagingService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initNavigation()
        initPrivateKey()
        initSharedPreference()

        MyFirebaseMessagingService().getFirebaseToken()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.navBar, findNavController(R.id.nav_host))
    }

    private fun initSharedPreference() {
        val isSet = MyApplication.prefs.getBoolean("isSet", false)

        if (!isSet) {
            val intent = Intent(this, SetDayActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initPrivateKey() {
        val getKey = MyApplication.prefs.getString("privateKey", "")

        if (getKey == "") {
            val randomString = generateRandomString(6)
            MyApplication.prefs.setString("privateKey", randomString)
        }

        Log.d("privateKey: " ,getKey)
    }

    private fun generateRandomString(length: Int): String {
        val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val randomString = (1..length)
            .map { charPool.random() }
            .joinToString("")
        return randomString
    }
}