package com.example.atm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atm.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var dbref: DatabaseReference
    private lateinit var userDBref: DatabaseReference

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var joinArrayList: ArrayList<Join>
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var userArrayList: ArrayList<UserAccount>

    private lateinit var originLauncher: ActivityResultLauncher<Intent>
    private lateinit var destinationLauncher: ActivityResultLauncher<Intent>
    private var mySearch = Search()

    // 비교할 거리 150 m
    private val distance = 150

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        originLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data != null) {
                    binding.searchOrigin.text = it.data!!.getStringExtra("name")
                    mySearch.apply {
                        originName = it.data!!.getStringExtra("name")!!
                        originRoad = it.data!!.getStringExtra("road")!!
                        originAddress = it.data!!.getStringExtra("address")!!
                        originX = it.data!!.getStringExtra("x")!!.toDouble()
                        originY = it.data!!.getStringExtra("y")!!.toDouble()
                    }
                }
                Log.d("LocalSearch", "${it.data}")
                Log.d("LocalSearch", "${mySearch}")
            }

        destinationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data != null) {
                    binding.searchDestination.text = it.data!!.getStringExtra("name")
                    mySearch.apply {
                        destinationName = it.data!!.getStringExtra("name")!!
                        destinationRoad = it.data!!.getStringExtra("road")!!
                        destinationAddress = it.data!!.getStringExtra("address")!!
                        destinationX = it.data!!.getStringExtra("x")!!.toDouble()
                        destinationY = it.data!!.getStringExtra("y")!!.toDouble()
                    }
                }
                Log.d("LocalSearch", "${it.data}")
                Log.d("LocalSearch", "${mySearch}")
            }

        val intent = Intent(activity, MapActivity::class.java)
        binding.searchOrigin.setOnClickListener {
            originLauncher.launch(intent)
        }
        binding.searchDestination.setOnClickListener {
            destinationLauncher.launch(intent)
        }
        binding.closeButton.setOnClickListener {
            binding.searchOrigin.text = ""
            binding.searchDestination.text = ""
            mySearch = Search()
        }
        binding.registerBtn.setOnClickListener {
            startActivity(Intent(activity, RegisterActivity::class.java))
        }
        binding.searchBtn.setOnClickListener {
            showItemList()
        }

        showItemList()
    }

    private fun showItemList() {
        getUserData()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = binding.recycler
        // Divider 추가
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = MyAdapter(joinArrayList)
        recyclerView.adapter = adapter

        // 리스트 아이템 클릭 시
        adapter.setItemClickListener(object : MyAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                Log.d(
                    "db",
                    "join: ${joinArrayList[position]}, Position: $position"
                )
                Log.d(
                    "db",
                    "post: ${postArrayList[position]}, Position: $position"
                )
                if (postArrayList[position].currentNumberPeople == postArrayList[position].requestNumberPeople) {
                    Toast.makeText(context, "모집이 만료되었습니다!", Toast.LENGTH_LONG).show()
                } else {
                    val intent = Intent(activity, DetailActivity::class.java)
                    intent.putExtra("post", postArrayList[position])
                    intent.putExtra("join", joinArrayList[position])
                    intent.putExtra("userAccount", userArrayList)
                    startActivity(intent)
                }
            }

        })
    }

    private fun getUserData() {
        postArrayList = arrayListOf<Post>()
        joinArrayList = arrayListOf<Join>()
        userArrayList = arrayListOf<UserAccount>()

        val currentUser = auth.currentUser

        dbref = FirebaseDatabase.getInstance().getReference("Posting")
        if (currentUser != null) {
            userDBref = FirebaseDatabase.getInstance().getReference("Around-Taxi-Member")
                .child("UserAccount").child(currentUser.uid)
        }

        userDBref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var userData = snapshot.getValue(UserAccount::class.java)
                    if (userData != null) {
                        userArrayList.add(userData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val getData = userSnapshot.getValue(Post::class.java)
                        if (getData != null) {
                            postArrayList.add(getData)
                        }
                        Log.d("post", "post: $getData")

                        if (mySearch.originName.isNullOrEmpty() || mySearch.destinationName.isNullOrEmpty()) {
                            joinArrayList.add(
                                Join(
                                    profileImage = R.drawable.profile,
                                    nickname = getData!!.nickname,
                                    origin = getData.search.originName.toString(),
                                    destination = getData.search.destinationName.toString(),
                                    currentNumberPeople = getData.currentNumberPeople,
                                    requestNumberPeople = getData.requestNumberPeople
                                )
                            )
                        } else {
                            val oriLat1 = mySearch.originY!!.toDouble()
                            val oriLon1 = mySearch.originX!!.toDouble()
                            val oriLat2 = getData!!.search.originY!!.toDouble()
                            val oriLon2 = getData.search.originX!!.toDouble()

                            val destLat1 = mySearch.destinationY!!.toDouble()
                            val destLon1 = mySearch.destinationX!!.toDouble()
                            val destLat2 = getData.search.destinationY!!.toDouble()
                            val destLon2 = getData.search.destinationX!!.toDouble()

                            val oriDistance =
                                DistanceManager.getDistance(oriLat1, oriLon1, oriLat2, oriLon2)
                            Log.d(
                                "distance",
                                "${mySearch.originName} 과 ${getData.search.originName} 거리 (m): $oriDistance"
                            )

                            val destDistance =
                                DistanceManager.getDistance(destLat1, destLon1, destLat2, destLon2)
                            Log.d(
                                "distance",
                                "${mySearch.destinationName} 과 ${getData.search.destinationName} 거리 (m): $destDistance"
                            )

                            if (oriDistance <= distance && destDistance <= distance) {
                                joinArrayList.add(
                                    Join(
                                        profileImage = R.drawable.profile,
                                        nickname = "test",
                                        origin = getData!!.search.originName.toString(),
                                        destination = getData.search.destinationName.toString(),
                                        currentNumberPeople = getData.currentNumberPeople,
                                        requestNumberPeople = getData.requestNumberPeople
                                    )
                                )
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}