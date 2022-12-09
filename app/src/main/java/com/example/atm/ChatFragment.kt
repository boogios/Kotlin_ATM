package com.example.atm

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private var userAccountDatabaseReference =
        FirebaseDatabase.getInstance().getReference("Around-Taxi-Member").child("UserAccount")
    private var postingDatabaseReference =
        FirebaseDatabase.getInstance().getReference("Posting")

    // Firebase Firestore 초기화
    private var chatDB = FirebaseFirestore.getInstance()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatList = arrayListOf<ChatLayout>() // 리사이클러뷰 리스트
    private lateinit var adapter: ChatAdapter // 리사이클러뷰 어댑터
    private lateinit var currentUser: String
    private lateinit var chatRoomName: String
    private lateinit var myLikes: String
    private lateinit var currentNumberOfPeople: String

    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            currentUser = it.getString("nickname").toString()
            chatRoomName = it.getString("chatroom").toString()
            myLikes = it.getString("likes").toString()
            currentNumberOfPeople = it.getString("currentNumberOfPeople").toString()
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
            Toast.makeText(context, "채팅방이 아직 없습니다", Toast.LENGTH_SHORT).show()
            binding.btnSendMessage.isEnabled = false
            binding.editTextMessage.isEnabled = false
            binding.txtRoomInfo.text = "No chat room yet"
            binding.btnLeaveChatRoom.visibility = View.INVISIBLE
        } else {
            chatDB.collection("${chatRoomName}'s ChatRoom")
                .orderBy("time", Query.Direction.ASCENDING)
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
        binding.rvMessageList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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

            chatDB.collection("${chatRoomName}'s ChatRoom").document(
                LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ).set(data)
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
                var chatDB =
                    FirebaseFirestore.getInstance().collection("${chatRoomName}'s ChatRoom Member")
                        .document("Chat Member")
                chatDB.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("chat", "DocumentSnapshot data: ${document.data}")
                            // D/chat: DocumentSnapshot data: {member2=ch2, PostOwner=tkddn, member3=, member1=ch1}
                            showDialog(document)

                        } else {
                            Log.d("chat", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("chat", "get failed with ", exception)
                    }
                userAccountDatabaseReference.child(currentUser.uid).child("chatRoom")
                    .setValue("None")
                postingDatabaseReference.child(chatRoomName).child("currentNumberPeople")
                    .setValue(currentNumberOfPeople.toInt() - 1)

            }
        }

        return view
    }

    private fun showDialog(document: DocumentSnapshot) {
        var chatMemberList =
            document.data!!.values.filter { it != "" }.map { it.toString() }.toTypedArray()
        Log.d("chat", "DocumentSnapshot data: $chatMemberList")
        var itemList = chatMemberList
        var checkedItems = arrayListOf<String>()
        val builder = AlertDialog.Builder(mContext)

        builder.setTitle("좋아요❤️를 누르고 싶은 상대를 클릭하세요!")
            .setMultiChoiceItems(
                itemList,
                null,
                object : DialogInterface.OnMultiChoiceClickListener {
                    override fun onClick(
                        dialog: DialogInterface,
                        pos: Int,
                        isChecked: Boolean
                    ) {
                        if (isChecked) {
                            checkedItems.add(itemList[pos])
                        } else if (checkedItems.contains(itemList[pos])) {
                            checkedItems.remove(itemList[pos])
                        }
                    }
                })
            .setPositiveButton(
                "확인",
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        Log.d("chat", "checkedItem: ${checkedItems}")
                        userAccountDatabaseReference.addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(userUid: DataSnapshot) {
                                    for (snapshot in userUid.children) {
                                        val userInfo: HashMap<String, Any> =
                                            snapshot.value as HashMap<String, Any>
                                        Log.d(
                                            "chat",
                                            "key: ${snapshot.key.toString()}, value: ${userInfo}"
                                        )
                                        val userNickname = userInfo.get("nickName")!!.toString()
                                        if (checkedItems.contains(userNickname)) {
                                            val userLike = userInfo.get("like")!!.toString().toInt()
                                            Log.d("chat", "userLike: ${userLike}")
                                            userAccountDatabaseReference.child(snapshot.key.toString())
                                                .child("like").setValue(userLike + 1)
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            }
                        )
                    }
                })

        builder.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}