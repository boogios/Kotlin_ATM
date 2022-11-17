package com.example.atm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.atm.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var originLauncher: ActivityResultLauncher<Intent>
    private lateinit var destinationLauncher: ActivityResultLauncher<Intent>
    private var mySearch = Search()
    private var currentCount:Int = 1
    private var totalCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // launcher
        originLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data != null) {
                    binding.registerOrigin.text = it.data!!.getStringExtra("name")
                    mySearch.apply {
                        originName = it.data!!.getStringExtra("name")!!
                        originRoad = it.data!!.getStringExtra("road")!!
                        originAddress = it.data!!.getStringExtra("address")!!
                        originX = it.data!!.getStringExtra("x")!!.toDouble()
                        originY = it.data!!.getStringExtra("y")!!.toDouble()
                    }
                }
                Log.d("registerSearch", "${it.data}")
                Log.d("registerSearch", "${mySearch}")
            }

        destinationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data != null) {
                    binding.registerDestination.text = it.data!!.getStringExtra("name")
                    mySearch.apply {
                        destinationName = it.data!!.getStringExtra("name")!!
                        destinationRoad = it.data!!.getStringExtra("road")!!
                        destinationAddress = it.data!!.getStringExtra("address")!!
                        destinationX = it.data!!.getStringExtra("x")!!.toDouble()
                        destinationY = it.data!!.getStringExtra("y")!!.toDouble()
                    }
                }
                Log.d("registerSearch", "${it.data}")
                Log.d("registerSearch", "${mySearch}")
            }


        // click events
        binding.closeBtn.setOnClickListener {
            finish()
        }
        val intent = Intent(this, MapActivity::class.java)
        binding.registerOrigin.setOnClickListener {
            originLauncher.launch(intent)
        }
        binding.registerDestination.setOnClickListener {
            destinationLauncher.launch(intent)
        }
        binding.downButton.setOnClickListener {
            minusCount()
        }
        binding.upButton.setOnClickListener {
            plusCount()
        }
        binding.registerButton.setOnClickListener {
            registerPost()
        }


    }

    private fun registerPost() {
        TODO("Not yet implemented")
    }

    private fun plusCount() {
        if (totalCount >= 4) return
        totalCount += 1
        binding.numOfPeople.setText(totalCount.toString() + "명")
    }

    private fun minusCount() {
        if (totalCount <= 0) return
        totalCount -= 1
        binding.numOfPeople.setText(totalCount.toString() + "명")
    }
}