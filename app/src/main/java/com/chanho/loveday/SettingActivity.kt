package com.chanho.loveday

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.view.View.OnLongClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chanho.loveday.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private var preferences: SharedPreferences? = null

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

        preferences = getSharedPreferences("setDDay", MODE_PRIVATE);

        val privateKey = preferences?.getString("privateKey", "") ?: ""

        binding.privateCodeLabelText.text = privateKey

        binding.privateCodeLabelText.setOnClickListener {
            val textToCopy = binding.privateCodeLabelText.text.toString()

            // 클립보드 관리자를 가져옴
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // 클립보드에 복사할 데이터 생성
            val clipData = ClipData.newPlainText("label", textToCopy)

            // 클립보드에 데이터를 복사
            clipboardManager.setPrimaryClip(clipData)

            // 복사되었다는 메시지 또는 피드백을 사용자에게 표시할 수 있음
            // 예를 들어 Toast 메시지를 사용하여 사용자에게 알릴 수 있음
             Toast.makeText(this, "텍스트가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }



}