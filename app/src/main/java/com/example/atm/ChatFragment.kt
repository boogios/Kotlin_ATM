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
import com.example.atm.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime DB
    private lateinit var databaseRef: DatabaseReference

    // Firebase Firestore 초기화
    private var chatDB = FirebaseFirestore.getInstance()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var registration: ListenerRegistration // 문서 수신할때 사용
    private val chatList = arrayListOf<ChatLayout>() // 리사이클러뷰 리스트
    private lateinit var adapter: ChatAdapter // 리사이클러뷰 어댑터
    private lateinit var currentUser: String
    private var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            currentUser = it.getString("nickname").toString()
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

        // 리사이클러뷰 설정
        binding.rvMessageList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = ChatAdapter(currentUser, chatList)
        binding.rvMessageList.adapter = adapter

        // 채팅창이 공백일 경우 -> 버튼 비활성화
        binding.editTextMessage.addTextChangedListener {
            binding.btnSendMessage.isEnabled = it.toString() != ""
            Toast.makeText(context, "텍스트를 입력하세요", Toast.LENGTH_SHORT).show()
        }

        binding.btnSendMessage.setOnClickListener {
            val data = hashMapOf(
                "nickname" to currentUser,
                "contents" to binding.editTextMessage.text.toString()
            )

            Toast.makeText(context, "클릭함", Toast.LENGTH_SHORT).show()

            chatDB.collection("Chat").add(data)
                .addOnSuccessListener {
                    binding.editTextMessage.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "전송 실패", Toast.LENGTH_LONG).show()
                    Log.d("ITM", "$e")
                }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatList.add(ChatLayout("알림", "$currentUser enter the chat room"))

        registration = chatDB.collection("Chat")
            .addSnapshotListener { snapshots, e ->
                if (e != null) { // 오류 발생 시
                    return@addSnapshotListener
                }

                // 원하지 않는 문서 무시
                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

                // 문서 수신
                for (doc in snapshots.documentChanges) {

                    // 문서가 추가될 경우 리사이클러뷰에 추가
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val nickname = doc.document["nickname"].toString()
                        val contents = doc.document["contents"].toString()

                        val item = ChatLayout(nickname, contents)
                        chatList.add(item)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        registration.remove()
    }
}