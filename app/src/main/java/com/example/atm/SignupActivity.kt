package com.example.atm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isInvisible
import com.example.atm.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime DB
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Around-Taxi-Member")

        // Password Check
        checkCorrectPassword()

        // Create Account Button
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val passwordCheck = binding.signupPasswordCheck.text.toString()
            val male = binding.signupMale
            val female = binding.signupFemale
            val nickname = binding.signupNickname.text.toString()

            createAccount(email, password, passwordCheck, male, female, nickname)
        }

        // 뒤로가기 버튼
        binding.btnBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    // 계정 생성 함수
    private fun createAccount(id: String, password: String, passwordCheck: String, male: RadioButton, female: RadioButton, nickname: String) {
        var gender: String = ""
        if (id.isNotEmpty() && password.isNotEmpty()) {
            if (password.length < 6) {
                Toast.makeText(this, "비밀번호는 6글자 이상 이여야 합니다", Toast.LENGTH_LONG).show()
            }
            if (password != passwordCheck) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show()
            }
            if (male.isChecked) {
                gender = male.text.toString()
            } else if (female.isChecked) {
                gender = female.text.toString()
            } else {
                Toast.makeText(this, "성별을 골라주세요", Toast.LENGTH_LONG).show()
            }
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_LONG).show()
            } else {
                auth?.createUserWithEmailAndPassword(id, password)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val account = UserAccount(id, password, gender, nickname)

                            if (currentUser != null) {
                                // Firebase Realtime Database 에 유저 정보 저장
                                // Around-Taxi-Member > UserAccount > UID(유저 고유 ID) > 유저 입력정보
                                databaseRef.child("UserAccount").child(currentUser.uid).setValue(account)
                            }
                            Toast.makeText(this, "계정 생성 완료", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_LONG).show()
                            // 에러 체크
                            // Log.d("ITM", it.exception.toString())
                        }
                    }
            }
        } else {
            Toast.makeText(this, "ID를 입력해주세요", Toast.LENGTH_LONG).show()
        }
    }

    // 비밀번호 체크 함수
    private fun checkCorrectPassword() {
        binding.signupPasswordCheck.addTextChangedListener(object : TextWatcher {

            // 문자 입력 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            // 문자 변경 후
            override fun afterTextChanged(p0: Editable?) {

                val password = binding.signupPassword.text.toString()
                val passwordCheck = binding.signupPasswordCheck.text.toString()
                val txtCheck = binding.txtPasswordCheck

                if (password != passwordCheck) {
                    txtCheck.isInvisible = false
                    txtCheck.setText("Password is not correct.. :(")
                } else {
                    txtCheck.setText("Password is correct!! :)")
                }
            }

            // 문자 변경할 때마다 호출
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }
}