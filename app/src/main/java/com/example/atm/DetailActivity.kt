package com.example.atm

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.atm.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime DB
    private lateinit var databaseRef: DatabaseReference
    private lateinit var postingRef: DatabaseReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val joinData = intent.getSerializableExtra("join") as Join
        val postData = intent.getSerializableExtra("post") as Post


        binding.detailOrigin.text = postData.search.originName
        binding.detailDestination.text = postData.search.destinationName

        val currentMember = postData.currentNumberPeople
        val requestMember = postData.requestNumberPeople

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Around-Taxi-Member")
        postingRef = FirebaseDatabase.getInstance().getReference("Posting")

        binding.detailCurrentMember.text =
            currentMember.toString() + " / " + requestMember.toString()

        binding.detailComment.text = postData.comment

        binding.joinButton.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                databaseRef.child("UserAccount").child(currentUser.uid).child("chatRoom")
                    .setValue(joinData.nickname)
                postingRef.child(postData.nickname).child("currentNumberPeople")
                    .setValue(postData.currentNumberPeople+1)
                Toast.makeText(baseContext, "참여하였습니다.", Toast.LENGTH_LONG).show()
                finish()
            }

        }
        binding.detailCloseBtn.setOnClickListener {
            finish()
        }


    }
}