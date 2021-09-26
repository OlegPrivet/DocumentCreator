package com.olegdev.documentcreator.fragments.homefragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.olegdev.documentcreator.MainActivity
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.adapters.PdfFilesAdapter
import com.olegdev.documentcreator.adapters.baseadapter.BaseAdapterCallback
import com.olegdev.documentcreator.adapters.diffutils.ListDiffUtils
import com.olegdev.documentcreator.managers.PickManager
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.utils.PermUtils
import com.olegdev.documentcreator.viewmodels.FileListViewModel

class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.simpleName

    private lateinit var fileListViewModel: FileListViewModel
    private lateinit var pickManager: PickManager
    private lateinit var filesAdapter: PdfFilesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileListViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(FileListViewModel::class.java)
        pickManager = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(PickManager::class.java)
        if (PermUtils.hasPermissions(requireActivity().applicationContext)) {
            pickManager.searchDocs()
        }
        sharedPref = (context as AppCompatActivity).getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        appBarLayout = view.findViewById(R.id.app_bar)
        progressBar = view.findViewById(R.id.progress_bar)
        (activity as MainActivity).setToolbar(toolbar= toolbar)
        toolbar.inflateMenu(R.menu.search_menu)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        filesAdapter = PdfFilesAdapter()
        recyclerView.adapter = filesAdapter

        filesAdapter.attachCallback(object : BaseAdapterCallback<Document> {
            override fun onItemClick(model: Document, view: View) {
                when(view.tag){
                    "btn_more" -> morePdf()
                    else -> seePdf(model)
                }
            }

            override fun onLongClick(model: Document, view: View): Boolean {
                return false
            }

        })
        fab = view.findViewById(R.id.fab)
        if (!PermUtils.hasPermissions(requireActivity().applicationContext)) {
            activityResultLauncher.launch((PermUtils.PERMISSIONS))
        }else{
            initView()
        }
        setHasOptionsMenu(true)
    }

    private fun seePdf(document: Document){
        val action = HomeFragmentDirections.actionHomeFragmentToPdfsFragment(docId = document.uuid)
        Navigation.findNavController(requireActivity().findViewById(R.id.home_root_view)).navigate(action)
    }

    private fun morePdf(){
        Log.e(TAG, "btn_more in recyclerView item")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                appBarLayout.setExpanded(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.file_name)
        searchView.setOnCloseListener {
            appBarLayout.setExpanded(true)
            return@setOnCloseListener false
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean { return true }

            override fun onQueryTextChange(newText: String): Boolean {
                searchDatabase(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        fileListViewModel.searchDatabase(searchQuery).observe(this, { list ->
            list.let {
                setAdapter(list= it)
            }
        })
    }

    private fun initView(){
        progressBar.visibility = View.VISIBLE
        fileListViewModel.fileListLiveData.observe(viewLifecycleOwner, Observer { files ->
            setAdapter(list= files)
        })
    }

    private fun setAdapter(list: List<Document>){
        val listDiffUtils = ListDiffUtils(filesAdapter.getData(), list)
        val diffRes = DiffUtil.calculateDiff(listDiffUtils)
        filesAdapter.setList(list)
        diffRes.dispatchUpdatesTo(filesAdapter)
        progressBar.visibility = View.GONE
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var granted = false
        permissions.entries.forEach {
            val isGranted = it.value
            if (isGranted) {
                granted = true
            } else {
                granted = false
                return@forEach
            }
        }
        if (granted) {
            pickManager.searchDocs()
            initView()
        }
    }

    override fun onDestroyView() {
        (activity as MainActivity?)!!.setToolbar(null)
        super.onDestroyView()
    }
}