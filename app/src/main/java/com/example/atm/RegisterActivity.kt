package com.example.atm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.atm.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var originLauncher: ActivityResultLauncher<Intent>
    private lateinit var destinationLauncher: ActivityResultLauncher<Intent>
    private var mySearch = Search()
    private var currentNumberPeople: Int = 1
    private var requestNumberPeople: Int = 0

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime DB
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference()

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

        val currentUser = auth.currentUser
        var nickname = ""

        if (currentUser != null) {
            databaseRef.child("Around-Taxi-Member").child("UserAccount").child(currentUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        nickname = snapshot.child("nickName").getValue().toString()

                        databaseRef.child("Around-Taxi-Member").child("UserAccount")
                            .child(currentUser.uid).child("chatRoom")
                            .setValue(nickname)

                        if (mySearch.originName.equals(null) or mySearch.destinationName.equals(null)) {
                            return Toast.makeText(
                                baseContext,
                                "출발지 또는 도착지를 입력해주세요!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        val myPost = Post(
                            search = mySearch,
                            currentNumberPeople = currentNumberPeople,
                            requestNumberPeople = requestNumberPeople,
                            comment = binding.comment.text.toString(),
                            nickname = nickname,
                            uid = currentUser.uid
                        )

                        if (currentUser != null) {
                            databaseRef.child("Posting").child(currentUser.uid).setValue(myPost)
                            Toast.makeText(baseContext, "모집글이 등록 되었습니다.", Toast.LENGTH_LONG).show()
                            Log.d("registerSearch", "$myPost")
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }


    }

    private fun plusCount() {
        if (requestNumberPeople >= 4) return
        requestNumberPeople += 1
        binding.numOfPeople.setText(requestNumberPeople.toString() + "명")
    }

    private fun minusCount() {
        if (requestNumberPeople <= 0) return
        requestNumberPeople -= 1
        binding.numOfPeople.setText(requestNumberPeople.toString() + "명")
    }
}