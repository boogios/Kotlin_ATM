package com.example.atm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.atm.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()

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
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "로그인 성공", Toast.LENGTH_LONG).show()
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

}