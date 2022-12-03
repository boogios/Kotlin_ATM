package com.example.atm

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

    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var joinArrayList: ArrayList<Join>

    private lateinit var imageId: Array<Int>
    private lateinit var nickname: Array<String>
    private lateinit var origin: Array<String>
    private lateinit var destination: Array<String>
    private lateinit var numberOfMember: Array<Int>

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
//        dataInitialize2()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = binding.recyclerView
        // Divider 추가
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = MyAdapter(joinArrayList)
        recyclerView.adapter = adapter
    }

    private fun dataInitialize2() {
        joinArrayList = arrayListOf<Join>()

        imageId = arrayOf(
            R.drawable.profile,
            R.drawable.profile,
            R.drawable.profile,
            R.drawable.profile,
            R.drawable.profile,
        )
        nickname = arrayOf(
            "apple",
            "apple",
            "apple",
            "apple",
            "apple",
        )
        origin = arrayOf(
            "공릉역",
            "공릉역",
            "공릉역",
            "공릉역",
            "공릉역",
        )
        destination = arrayOf(
            "서울과학기술대학교 정문",
            "서울과학기술대학교 정문",
            "서울과학기술대학교 정문",
            "서울과학기술대학교 정문",
            "서울과학기술대학교 정문",
        )
        numberOfMember = arrayOf(
            1,
            1,
            1,
            1,
            1,
        )

        for (i in imageId.indices) {
            val join = Join(imageId[i], nickname[i], origin[i], destination[i], numberOfMember[i])
            Log.d("ITM", join.toString())
            joinArrayList.add(join)

        }


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
                    Log.d("ITM", getData.toString())
                    val join = Join(
                        R.drawable.profile,
                        nickname,
                        getData?.originName.toString(),
                        getData?.destinationName.toString(),
                        currentUserNumber.toInt()
                    )
                    Log.d("ITM", join.toString())
                    joinArrayList.add(join)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
}