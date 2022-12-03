package com.example.atm

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atm.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    private lateinit var joinArrayList: ArrayList<Join>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()

        databaseRef = FirebaseDatabase.getInstance().getReference("Around-Taxi-Member")

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            Toast.makeText(activity, "로그아웃", Toast.LENGTH_LONG).show()
        }

        dataInitialize(databaseRef)

    }

    private fun dataInitialize(Ref: DatabaseReference) {
        joinArrayList = arrayListOf()

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()

        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nickname = snapshot.child("UserAccount").child(auth.currentUser?.uid.toString()).child("nickName").getValue().toString()
                    var getData =
                        snapshot.child("Posting").child(auth.currentUser?.uid.toString()).child("search").getValue(Search::class.java)
                    val currentUserNumber = snapshot.child("Posting").child(auth.currentUser?.uid.toString())
                        .child("currentNumberPeople").getValue().toString()
                    val requestUserNumber = snapshot.child("Posting").child(auth.currentUser?.uid.toString())
                        .child("requestNumberPeople").getValue().toString()

                    binding.txtSettingNickname.text = nickname
                    binding.txtOrigin.text = getData?.originName.toString()
                    binding.txtDestination.text = getData?.destinationName.toString()
                    binding.txtCurrentByRequest.text = "$currentUserNumber/$requestUserNumber"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
}