package com.example.practice_3week

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.practice_3week.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ITM", "onCreate Called!")
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.yujinIsCute.text = "유진이는 진짜 너무너무 귀여웡"

        binding.yujinIsCute.setOnClickListener {
            Toast.makeText(this.applicationContext, "알앙!", Toast.LENGTH_SHORT).show()
        }

//        val btn_YuJin: Button = findViewById(R.id.yujinIsCute)
//        btn_YuJin.text = "유진이는 진짜 너무너무 귀여웡"
//
//        btn_YuJin.setOnClickListener {
//            Toast.makeText(this.applicationContext, "알앙!", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("ITM", "onStart Called!")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ITM", "onResume Called!")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ITM", "onPause Called!")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ITM", "onStop Called!")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ITM", "onDestory Called!")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("ITM", "onRestart Called!")
    }

}