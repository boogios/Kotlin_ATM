package com.example.practice_4week

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practice_4week.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoRoot.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnReply.setOnClickListener {
            intent.putExtra("grade", "${binding.txtReply.text}")
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}