package com.example.atm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atm.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private var userAccountDatabaseReference = FirebaseDatabase.getInstance().getReference("Around-Taxi-Member").child("UserAccount")
    // Firebase Firestore 초기화
    private var chatDB = FirebaseFirestore.getInstance()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatList = arrayListOf<ChatLayout>() // 리사이클러뷰 리스트
    private lateinit var adapter: ChatAdapter // 리사이클러뷰 어댑터
    private lateinit var currentUser: String
    private lateinit var chatRoomName: String
    private lateinit var myLikes: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            currentUser = it.getString("nickname").toString()
            chatRoomName = it.getString("chatroom").toString()
            myLikes = it.getString("likes").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root

        Toast.makeText(context, "현재 닉네임은 ${currentUser}입니다.", Toast.LENGTH_SHORT).show()

        if (chatRoomName == "None") {
            Toast.makeText(context, "채팅방이 아직 없습니다",Toast.LENGTH_SHORT).show()
            binding.btnSendMessage.isEnabled = false
            binding.editTextMessage.isEnabled = false
            binding.txtRoomInfo.text = "No chat room yet"
            binding.btnLeaveChatRoom.visibility = View.INVISIBLE
        } else {
            chatDB.collection("${chatRoomName}'s ChatRoom").orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        binding.txtRoomInfo.text = "${chatRoomName}'s ChatRoom"
                        binding.btnLeaveChatRoom.visibility = View.VISIBLE
                        for (document in snapshot.documentChanges) {
                            if (document.type == DocumentChange.Type.ADDED) {
                                val nickname = document.document["nickname"].toString()
                                val contents = document.document["contents"].toString()
                                val time = document.document["time"].toString()
                                val likes = document.document["likes"].toString()

                                val item = ChatLayout(nickname, contents, time, likes)
                                chatList.add(item)
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
        }

        // 리사이클러뷰 설정
        binding.rvMessageList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = ChatAdapter(currentUser, chatList)
        binding.rvMessageList.adapter = adapter

        // 채팅창이 공백일 경우 -> 버튼 비활성화
        binding.editTextMessage.addTextChangedListener {
            binding.btnSendMessage.isEnabled = it.toString() != ""
        }

        binding.btnSendMessage.setOnClickListener {
            val data = hashMapOf(
                "nickname" to currentUser,
                "contents" to binding.editTextMessage.text.toString(),
                "time" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "likes" to myLikes
            )

            chatDB.collection("${chatRoomName}'s ChatRoom").add(data)
                .addOnSuccessListener {
                    binding.editTextMessage.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "전송 실패", Toast.LENGTH_LONG).show()
                    Log.d("ITM", "$e")
                }
        }



        binding.btnLeaveChatRoom.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                userAccountDatabaseReference.child(currentUser.uid).child("chatRoom").setValue("None")
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}