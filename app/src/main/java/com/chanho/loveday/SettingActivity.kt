package com.chanho.loveday

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chanho.loveday.databinding.ActivityMainBinding
import com.chanho.loveday.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingCloseButton.setOnClickListener {
            val resultIntent = Intent()
            // 결과 데이터 추가
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // 현재 액티비티 종료
        }
    }
}