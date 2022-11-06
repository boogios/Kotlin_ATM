package com.example.practice_4week

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.practice_4week.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Toast.makeText(this, it.data?.getStringExtra("grade"), Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoSub.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            requestLauncher.launch(intent)
        }
//
//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//            Log.d("ITM", "requesCode: $requestCode resultcode: $resultCode")
//            Log.d("TIM", "${data?.getStringExtra("grade")}")
//            Toast.makeText(this, data?.getStringExtra("grade"), Toast.LENGTH_SHORT).show()
//        }

        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:029706468")
            }
            startActivity(intent)
        }

        binding.btnMap.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:37.63177, 127.077") // geo:0,0?q=seoul+national+university+of+science+and+technology
            }
            startActivity(intent)
        }

        binding.btnWebSearch.setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY,"seoultech ITM")
            }
            startActivity(intent)
        }

        binding.btnSend.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpg"
            }
            startActivity(intent)
        }

        binding.btnChooser.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
                .run { createChooser(this, "Show me the Picture!") }
            // val chooser = Intent.createChooser(intent, "Show me the Picture!")
            startActivity(intent) // chooser
        }

        binding.btnException.setOnClickListener {
            val intent = Intent("action_itm").apply {
                type = "seoultech/ITM"
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.d("ITM", "no apps found!")
            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ITM", "requestCode: $requestCode resultCode: $resultCode")
        Log.d("ITM", "${data?.getStringExtra("grade")}")
        Toast.makeText(this, data?.getStringExtra("grade"), Toast.LENGTH_SHORT).show()
    }
}