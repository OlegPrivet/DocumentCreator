package com.olegdev.documentcreator.fragments.homefragments

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.MediaColumns.*
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.olegdev.documentcreator.MainActivity
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.adapters.PdfFilesAdapter
import com.olegdev.documentcreator.adapters.baseadapter.BaseAdapterCallback
import com.olegdev.documentcreator.adapters.diffutils.ListDiffUtils
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.states.FileState
import com.olegdev.documentcreator.utils.PermUtils
import com.olegdev.documentcreator.viewmodels.FileListViewModel
import java.util.*

class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.simpleName

    private lateinit var fileListViewModel: FileListViewModel
    private lateinit var filesAdapter: PdfFilesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileListViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(FileListViewModel::class.java)
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
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        filesAdapter = PdfFilesAdapter()
        recyclerView.adapter = filesAdapter
        fileListViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is FileState.DefaultState -> {
                    progressBar.visibility = View.GONE
                }
                is FileState.FileStateUpload -> {
                    progressBar.visibility = View.VISIBLE
                }
                is FileState.FileStateDownload -> {
                    fileListViewModel.setState(FileState.DefaultState())
                    seePdf(state.uuid)
                }
                is FileState.FileStateError -> {
                    fileListViewModel.setState(FileState.DefaultState())
                    showShack(state.message)
                }
                is FileState.FileStateMoreDialog -> {
                    fileListViewModel.moreFun((activity as MainActivity), state.document)
                }
            }
        })
        initView()
        itemClick()
        initToolbar()
        initFabButton(view)
        setHasOptionsMenu(true)
        getIntentData()
    }

    private fun initFabButton(rootView: View) {
        fab = rootView.findViewById(R.id.fab)
        fab.setOnClickListener {
            if (PermUtils.requestPerms(requireActivity(), PermUtils.PERMISSIONS_STORAGE, fab)) {
                (activity as MainActivity).bottomView.selectedItemId = R.id.file_explorer_nav_graph
            }
        }
    }

    private fun seePdf(uuid: UUID) {
        val action = HomeFragmentDirections.actionHomeFragmentToPdfsFragment(docId = uuid)
        (activity as MainActivity).navController.navigate(action)
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        fileListViewModel.searchDatabase(searchQuery).observe(this, { list ->
            list.let {
                setAdapter(list = it)
            }
        })
    }

    private fun initView() {
        progressBar.visibility = View.VISIBLE
        fileListViewModel.fileListLiveData.observe(viewLifecycleOwner, Observer { files ->
            setAdapter(list = files)
        })
    }

    private fun initToolbar() {
        toolbar.inflateMenu(R.menu.search_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_search -> {
                    appBarLayout.setExpanded(false)
                    true
                }
                else -> false
            }
        }
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.file_name)
        searchView.setOnCloseListener {
            appBarLayout.setExpanded(true)
            return@setOnCloseListener false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchDatabase(newText)
                return true
            }
        })
    }

    private fun setAdapter(list: List<Document>) {
        val listDiffUtils = ListDiffUtils(filesAdapter.getData(), list)
        val diffRes = DiffUtil.calculateDiff(listDiffUtils)
        filesAdapter.setList(list)
        diffRes.dispatchUpdatesTo(filesAdapter)
        progressBar.visibility = View.GONE
    }

    private fun itemClick() {
        filesAdapter.attachCallback(object : BaseAdapterCallback<Document> {
            override fun onItemClick(model: Document, view: View) {
                when (view.tag) {
                    "btn_more" -> fileListViewModel.moreDialog(model)
                    else -> seePdf(model.uuid)
                }
            }

            override fun onLongClick(model: Document, view: View): Boolean {
                return false
            }

        })
    }

    private fun getIntentData() {
        val data: Uri? = activity?.intent?.data
        if (activity?.intent?.type?.startsWith("application/pdf") == true) {
            data?.let {
                if (PermUtils.requestPerms(requireActivity(), PermUtils.PERMISSIONS_STORAGE, fab)) {
                    fileListViewModel.addFile(it)
                    activity?.intent = null
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fileListViewModel.checkFiles()
    }

    private fun showShack(message: Int) {
        Snackbar.make(
            requireActivity().findViewById(R.id.main_view),
            getString(message),
            Snackbar.LENGTH_SHORT
        ).setAnchorView(fab).show()
    }
}