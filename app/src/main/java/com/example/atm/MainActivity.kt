package com.example.atm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.atm.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime DB
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView2.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.chat -> {
                    // FirebaseAuth
                    auth = FirebaseAuth.getInstance()
                    databaseRef = FirebaseDatabase.getInstance().reference

                    databaseRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nickname = snapshot.child("Around-Taxi-Member").child("UserAccount")
                                .child(auth.currentUser?.uid.toString()).child("nickName")
                                .getValue().toString()
                            val chatRoom = snapshot.child("Around-Taxi-Member").child("UserAccount")
                                .child(auth.currentUser?.uid.toString()).child("chatRoom")
                                .getValue().toString()
                            val likes = snapshot.child("Around-Taxi-Member").child("UserAccount")
                                .child(auth.currentUser?.uid.toString()).child("like")
                                .getValue().toString()
                            val currentNumberOfPeople = snapshot.child("Posting").child(chatRoom)
                                .child("currentNumberPeople").getValue().toString()
                            val bundle = Bundle()
                            bundle.putString("nickname", nickname)
                            bundle.putString("chatroom", chatRoom)
                            bundle.putString("likes", likes)
                            bundle.putString("currentNumberOfPeople", currentNumberOfPeople)
                            replaceChatFragment(bundle)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // 읽어오기 실패했을 때
                        }
                    })
                }

                R.id.settings -> {
                    auth = FirebaseAuth.getInstance()
                    databaseRef = FirebaseDatabase.getInstance().reference

                    databaseRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nickname = snapshot.child("Around-Taxi-Member").child("UserAccount")
                                .child(auth.currentUser?.uid.toString()).child("nickName")
                                .getValue().toString()
                            val chatRoom = snapshot.child("Around-Taxi-Member").child("UserAccount")
                                .child(auth.currentUser?.uid.toString()).child("chatRoom")
                                .getValue().toString()
                            val currentNumberOfPeople = snapshot.child("Posting").child(chatRoom)
                                .child("currentNumberPeople").getValue().toString()
                            val likes = snapshot.child("Around-Taxi-Member").child("UserAccount")
                                .child(auth.currentUser?.uid.toString()).child("like")
                                .getValue().toString()
                            val bundle = Bundle()
                            bundle.putString("nickname", nickname)
                            bundle.putString("chatroom", chatRoom)
                            bundle.putString("currentNumberOfPeople", currentNumberOfPeople)
                            bundle.putString("likes", likes)
                            replaceSettingFragment(bundle)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // 읽어오기 실패했을 때
                        }
                    })
                }

                else -> {

                }

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun replaceChatFragment(bundle: Bundle) {
        val destination = ChatFragment()
        destination.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, destination)
            .commitAllowingStateLoss()
    }

    private fun replaceSettingFragment(bundle: Bundle) {
        val destination = SettingFragment()
        destination.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, destination)
            .commitAllowingStateLoss()
    }
}