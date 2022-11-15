package com.example.atm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

// Fragment 에서는 재사용을 위해 View 들을 메모리에 보관한다.
// onDestroy 가 호출되어도 내부적으로 메모리를 보관하고 있기 때문에
// View Binding 에 대한 참조를 null 로 만들어주지 않으면 메모리 누수가 발생함
// BaseFragment 를 만들어서 각 Fragment 마다 상속하여 사용하면 편함
typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB: ViewBinding>(
    private val inflate: Inflate<VB>
    ) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}