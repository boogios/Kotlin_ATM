package com.example.atm

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.example.atm.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    private lateinit var joinArrayList: ArrayList<Join>
    private lateinit var nickName: String
    private lateinit var chatRoomName: String
    private lateinit var currentNumberOfPeople: String
    private lateinit var myLikes: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            nickName = it.getString("nickname").toString()
            chatRoomName = it.getString("chatroom").toString()
            currentNumberOfPeople = it.getString("currentNumberOfPeople").toString()
            myLikes = it.getString("likes").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FirebaseAuth
        auth = FirebaseAuth.getInstance()

        databaseRef = FirebaseDatabase.getInstance().reference

        binding.layoutJoinedInfo.isInvisible = false
        binding.txtNotYetJoined.isInvisible = false

        binding.settingName.text = "Welcome, $nickName!"
        binding.settingHeartText.setText("내 좋아요 수: $myLikes")

        binding.btnLeaveChatRoomInSetting.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                databaseRef.child("Around-Taxi-Member").child("UserAccount").child(currentUser.uid)
                    .child("chatRoom").setValue("None")
                databaseRef.child("Posting").child(chatRoomName).child("currentNumberPeople")
                    .setValue(currentNumberOfPeople.toInt() - 1)
                binding.layoutJoinedInfo.isInvisible = true
                binding.txtNotYetJoined.isInvisible = false
            }
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            Toast.makeText(activity, "로그아웃", Toast.LENGTH_LONG).show()
        }

        dataInitialize(databaseRef)

    }

    private fun dataInitialize(Ref: DatabaseReference) {
        joinArrayList = arrayListOf()

        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var getChatRoomData =
                        snapshot.child("Around-Taxi-Member").child("UserAccount")
                            .child(auth.currentUser?.uid.toString()).child("chatRoom").getValue()
                    var getData =
                        snapshot.child("Posting").child(getChatRoomData as String)
                            .child("search").getValue(Search::class.java)
                    if (getChatRoomData == "None") {
                        binding.layoutJoinedInfo.isInvisible = true
                        binding.txtNotYetJoined.isInvisible = false
                    } else {
                        binding.layoutJoinedInfo.isInvisible = false
                        binding.txtNotYetJoined.isInvisible = true
                        val nickname = snapshot.child("Around-Taxi-Member").child("UserAccount")
                            .child(auth.currentUser?.uid.toString()).child("nickName").getValue()
                            .toString()
                        val currentUserNumber =
                            snapshot.child("Posting").child(getChatRoomData)
                                .child("currentNumberPeople").getValue().toString()
                        val requestUserNumber =
                            snapshot.child("Posting").child(getChatRoomData)
                                .child("requestNumberPeople").getValue().toString()

                        binding.txtSettingNickname.text = nickname
                        binding.txtOrigin.text = getData?.originName.toString()
                        binding.txtDestination.text = getData?.destinationName.toString()
                        binding.txtCurrentByRequest.text = "$currentUserNumber/$requestUserNumber"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
}