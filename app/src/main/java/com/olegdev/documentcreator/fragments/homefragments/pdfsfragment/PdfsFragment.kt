package com.olegdev.documentcreator.fragments.homefragments.pdfsfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.material.appbar.MaterialToolbar
import com.olegdev.documentcreator.MainActivity
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.constants.SharedPrefConstant.AUTO_SPACING
import com.olegdev.documentcreator.constants.SharedPrefConstant.NIGHT_MODE
import com.olegdev.documentcreator.constants.SharedPrefConstant.PAGE_FLING
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.pdfscreenutils.customlinkhandler.CustomLinkHandler
import com.olegdev.documentcreator.pdfscreenutils.pdfscrollhandler.CustomScrollHandler
import com.olegdev.documentcreator.viewmodels.FileViewModel
import com.pixplicity.easyprefs.library.Prefs
import java.util.*







private const val BOTTOM_BAR_SHOW = "BOTTOM_BAR_SHOW"

class PdfsFragment : Fragment() {

    private val TAG = PdfsFragment::class.simpleName

    private lateinit var fileViewModel: FileViewModel
    private lateinit var document: Document
    private lateinit var docId: UUID
    private var currentPage = 0
    private lateinit var pdfView: PDFView
    private val args by navArgs<PdfsFragmentArgs>()

    private lateinit var toolbar: MaterialToolbar
    private var toolbarBarShow = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(FileViewModel::class.java)
        docId = args.docId
        fileViewModel.loadFile(docId)
        if (savedInstanceState != null){
            if (savedInstanceState.getBoolean(BOTTOM_BAR_SHOW)){
                toolbarBarShow = savedInstanceState.getBoolean(BOTTOM_BAR_SHOW)
                fileViewModel.setBarsShown(toolbarBarShow)
            }
        }else{
            fileViewModel.setBarsShown(toolbarBarShow)
        }
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BOTTOM_BAR_SHOW, toolbarBarShow)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pdfs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfView = view.findViewById(R.id.pdf_view)
        toolbar = view.findViewById(R.id.toolbar)
        (activity as MainActivity).setToolbar(toolbar = toolbar)
        toolbar.inflateMenu(R.menu.file_toolbar_menu)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).navController.popBackStack()
        }
        initDocument()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.day_night -> {
                val night_mode = Prefs.getBoolean(NIGHT_MODE, false)
                pdfView.setNightMode(!night_mode)
                pdfView.loadPages()
                Prefs.putBoolean(NIGHT_MODE, !night_mode)
                requireActivity().invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.file_toolbar_menu, menu)
        /*val day_night = menu.findItem(R.id.day_night)
        Log.e(TAG, "onPrepareOptionsMenu - ${Prefs.getBoolean(NIGHT_MODE, false)}")
        if (Prefs.getBoolean(NIGHT_MODE, false))
            day_night.icon = ContextCompat.getDrawable(pdfView.context, R.drawable.ic_light_mode)
        else
            day_night.icon = ContextCompat.getDrawable(pdfView.context, R.drawable.ic_dark_mode)
        super.onPrepareOptionsMenu(menu)*/
        onCreateOptionsMenu(menu, inflater)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.file_toolbar_menu, menu)
        val day_night = menu.findItem(R.id.day_night)
        Log.e(TAG, "onCreateOptionsMenu - ${Prefs.getBoolean(NIGHT_MODE, false)}")
        if (Prefs.getBoolean(NIGHT_MODE, false))
            day_night.icon = ContextCompat.getDrawable(pdfView.context, R.drawable.ic_light_mode)
        else
            day_night.icon = ContextCompat.getDrawable(pdfView.context, R.drawable.ic_dark_mode)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initDocument() {
        fileViewModel.fileLiveData.observe(viewLifecycleOwner, { document ->
            document?.let {
                this.document = document
                loadPdf(document)
            }
        })
        fileViewModel.barsShown.observe(viewLifecycleOwner, { show ->
            if (show) {
                toolbar.visibility = View.VISIBLE
            } else {
                toolbar.visibility = View.GONE
            }
            toolbarBarShow = show
        })
    }

    private fun loadPdf(document: Document) {
        currentPage = document.currentPage
        pdfView.useBestQuality(true)
        pdfView.fromUri(document.path)
            .defaultPage(currentPage)
            .onPageChange { page, pageCount ->
                toolbar.title = getString(R.string.page) + " ${page+1}/$pageCount"
                currentPage = page
            }
            .onTap {
                if (it.action == MotionEvent.ACTION_DOWN) {
                    fileViewModel.setBarsShown(!toolbarBarShow)
                    return@onTap true
                } else return@onTap false
            }
            .enableAnnotationRendering(true)
            .enableAntialiasing(true)
            .spacing(0)
            .pageFitPolicy(FitPolicy.BOTH)
            .fitEachPage(true)
            .enableSwipe(true)
            .enableDoubletap(true)
            .swipeHorizontal(false)
            .autoSpacing(Prefs.getBoolean(AUTO_SPACING, true))
            .pageFling(Prefs.getBoolean(PAGE_FLING, true))
            .nightMode(Prefs.getBoolean(NIGHT_MODE, false))
            .pageSnap(true)
            .linkHandler (CustomLinkHandler(pdfView))
            .scrollHandle(CustomScrollHandler(requireActivity().applicationContext, false))
            .load()
    }

    override fun onStop() {
        super.onStop()
        document.currentPage = currentPage
        document.date_modified = System.currentTimeMillis()
        fileViewModel.saveFile(document)
    }

    override fun onDestroyView() {
        (activity as MainActivity?)!!.setToolbar(null)
        super.onDestroyView()
    }
}