package com.example.atm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.atm.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime DB
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Around-Taxi-Member")

        // 회원가입 창으로 이동
        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.loginID.text.toString()
            val password = binding.loginPassword.text.toString()
            logIn(email, password)
        }
    }

//    public override fun onStart() {
//        super.onStart()
//        goToMainPage(auth?.currentUser) // 로그아웃 안했을 때 자동 로그인 기능
//    }

    private fun logIn(id: String, password: String) {
        if (id.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(id, password)
                ?.addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        getUserNickname(databaseRef)
                        goToMainPage(auth?.currentUser)
                    } else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // 파이어베이스에 유저 정보 전달 후 메인 페이지로 이동하는 함수
    private fun goToMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // User의 Nickname 가져오기
    private fun getUserNickname(Ref: DatabaseReference) {
        Ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.child("UserAccount").child(auth.currentUser?.uid.toString()).child("nickName").getValue().toString()
                Log.d("ITM", nickname)
                Toast.makeText(baseContext, "로그인 성공 ${nickname}", Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // 읽어오기 실패했을 때
            }
        })
    }

}