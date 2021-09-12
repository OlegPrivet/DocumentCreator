package com.olegdev.documentcreator.fragments.homefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.managers.PickManager
import com.olegdev.documentcreator.utils.PermUtils
import com.olegdev.documentcreator.viewmodels.DocViewModel


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var docViewModel: DocViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        docViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(DocViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!PermUtils.hasPermissions(requireActivity().applicationContext)) {
            activityResultLauncher.launch((PermUtils.PERMISSIONS))
        }else{
            initView()
        }
    }

    fun initView(){
        docViewModel.lvDocData.observe(viewLifecycleOwner, Observer { files ->
            //progressBar?.visibility = View.GONE

        })
        docViewModel.getDocs(PickManager.getFileTypes(), PickManager.sortingType.comparator)
    }

    override fun onResume() {
        super.onResume()

    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var granted = false
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                granted = true
            } else {
                granted = false
                return@forEach
            }
        }
        if (granted) docViewModel.getDocs(PickManager.getFileTypes(), PickManager.sortingType.comparator)
    }
}