package com.example.atm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.atm.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Create Account Button
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()

            createAccount(email, password)
        }

        // 뒤로가기 버튼
        binding.btnBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    // 계정 생성 함수
    private fun createAccount(id: String, password: String) {
        if (id.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(id, password)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "계정 생성 완료", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_LONG).show()
                        }
                    }
        }
    }
}