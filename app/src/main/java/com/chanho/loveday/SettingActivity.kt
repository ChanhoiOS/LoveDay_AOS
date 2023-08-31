package com.chanho.loveday

import android.app.Activity
import android.app.Application
import android.content.*
import android.os.Bundle
import android.view.View.OnLongClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chanho.loveday.application.MyApplication
import com.chanho.loveday.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setBtnEvent()
        setClipboard()
    }

    private fun initView() {
        val privateKey = MyApplication.prefs.getString("privateKey", "")

        binding.privateCodeLabelText.text = privateKey
    }

    private fun setBtnEvent() {
        binding.settingCloseButton.setOnClickListener {
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun setClipboard() {
        binding.privateCodeLabelText.setOnClickListener {
            val textToCopy = binding.privateCodeLabelText.text.toString()

            // 클립보드 관리자를 가져옴
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // 클립보드에 복사할 데이터 생성
            val clipData = ClipData.newPlainText("label", textToCopy)

            // 클립보드에 데이터를 복사
            clipboardManager.setPrimaryClip(clipData)
        }
    }
}