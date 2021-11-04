package com.olegdev.documentcreator.fragments.fileexplorerfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.adapters.FileExplorerAdapter
import com.olegdev.documentcreator.adapters.baseadapter.BaseAdapterCallback
import com.olegdev.documentcreator.constants.SharedPrefConstant.LIST_TYPE
import com.olegdev.documentcreator.utils.FilePickUtils
import com.olegdev.documentcreator.viewmodels.FileExplorerViewModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FileExplorerFragment : Fragment() {

    private val TAG = FileExplorerFragment::class.simpleName

    private lateinit var fileExplorerViewModel: FileExplorerViewModel
    private lateinit var filesAdapter: FileExplorerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileExplorerViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(FileExplorerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_file_explorer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout)
        appBarLayout = view.findViewById(R.id.app_bar)
        progressBar = view.findViewById(R.id.progress_bar)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        filesAdapter = FileExplorerAdapter()
        recyclerView.adapter = filesAdapter
        fetchFiles(path = fileExplorerViewModel.currentPath)
        initView()
        initToolbar()
        itemClick()
        setHasOptionsMenu(true)
    }

    private fun seePdf(uuid: UUID) {
        /*val action =
            FileExplorerFragmentDirections.actionFileExplorerFragmentToPdfsFragment2(docId = uuid)
        (activity as MainActivity).navController.navigate(action)*/
        progressBar.visibility = View.GONE
    }

    private fun initView() {
        progressBar.visibility = View.VISIBLE
        fileExplorerViewModel.files.observe(viewLifecycleOwner, Observer { files ->
            collapsingToolbarLayout.title = fileExplorerViewModel.arrPath.last().toString()
            if (fileExplorerViewModel.arrPath.size > 1)
                toolbar.setNavigationIcon(R.drawable.ic_back)
            else
                toolbar.navigationIcon = null
            setAdapter(list = files)
        })
    }

    private fun setAdapter(list: List<File>) {
        filesAdapter.setList(list)
        recyclerView.scrollToPosition(0)
        progressBar.visibility = View.GONE
    }

    private fun itemClick() {
        filesAdapter.attachCallback(object : BaseAdapterCallback<File> {
            override fun onItemClick(model: File, view: View) {
                progressBar.visibility = View.VISIBLE
                if (model.isDirectory) {
                    fetchFiles(model.path)
                }else {
                    val docId = fileExplorerViewModel.addFile(model)
                    seePdf(docId)
                }
            }

            override fun onLongClick(model: File, view: View): Boolean {
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setToolbarAction(Prefs.getBoolean(LIST_TYPE))
    }

    private fun initToolbar(){
        toolbar.inflateMenu(R.menu.file_explorer_toolbar_menu)
        toolbar.setNavigationOnClickListener {
            val paths = fileExplorerViewModel.arrPath
            paths.removeAt(paths.lastIndex)
            fetchFiles(path = FilePickUtils.getBackPath(paths))
        }
        toolbar.navigationIcon = null
        toolbar.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.action_list_type ->{
                    val list = !Prefs.getBoolean(LIST_TYPE)
                    Prefs.putBoolean(LIST_TYPE, list)
                    setToolbarAction(list)
                    true
                }
                else -> false

            }
        }
    }

    private fun setToolbarAction(list: Boolean) {
        Log.e(TAG, "List grid: $list")
        if (list) {
            recyclerView.layoutManager =
                LinearLayoutManager(requireActivity().applicationContext)
            toolbar.menu.findItem(R.id.action_list_type).icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_type_grid)
        } else {
            recyclerView.layoutManager =
                GridLayoutManager(requireActivity().applicationContext, 2)
            toolbar.menu.findItem(R.id.action_list_type).icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_type_list)
        }
    }

    private fun fetchFiles(path: String){
        CoroutineScope(Dispatchers.IO).launch {
            fileExplorerViewModel.getFiles(path = path)
        }
    }
}
