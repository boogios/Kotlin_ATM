package com.example.atm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.atm.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            Toast.makeText(activity, "로그아웃", Toast.LENGTH_LONG).show()
        }
    }
}