package com.example.atm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atm.databinding.FragmentHomeBinding
import com.google.firebase.database.DatabaseReference

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var joinArrayList: ArrayList<Join>

    private lateinit var imageId: Array<Int>
    private lateinit var nickname: Array<String>
    private lateinit var origin: Array<String>
    private lateinit var destination: Array<String>
    private lateinit var currentNumberPeople: Array<Int>
    private lateinit var requestNumberPeople: Array<Int>
    private lateinit var originLauncher: ActivityResultLauncher<Intent>
    private lateinit var destinationLauncher: ActivityResultLauncher<Intent>
    private var mySearch = Search()

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
            binding.searchOrigin.text=""
            binding.searchDestination.text=""
            mySearch = Search()
        }
        binding.registerBtn.setOnClickListener {
            startActivity(Intent(activity, RegisterActivity::class.java))
        }

        dataInitialize()

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

    }

    private fun dataInitialize() {
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
        currentNumberPeople = arrayOf(
            1,
            1,
            1,
            1,
            1,
        )
        requestNumberPeople= arrayOf(
            4,
            4,
            4,
            4,
            4,
        )

        for (i in imageId.indices) {
            val join = Join(
                imageId[i],
                nickname[i],
                origin[i],
                destination[i],
                currentNumberPeople[i],
                requestNumberPeople[i]
            )

            joinArrayList.add(join)

        }


    }
}