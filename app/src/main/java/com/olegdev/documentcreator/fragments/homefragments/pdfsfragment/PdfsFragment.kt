package com.olegdev.documentcreator.fragments.homefragments.pdfsfragment

import android.os.Bundle
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
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.pdfscreenutils.customlinkhandler.CustomLinkHandler
import com.olegdev.documentcreator.pdfscreenutils.pdfscrollhandler.CustomScrollHandler
import com.olegdev.documentcreator.viewmodels.FileViewModel
import com.pixplicity.easyprefs.library.Prefs
import java.io.*
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
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(BOTTOM_BAR_SHOW)) {
                toolbarBarShow = savedInstanceState.getBoolean(BOTTOM_BAR_SHOW)
                fileViewModel.setBarsShown(toolbarBarShow)
            }
        } else {
            fileViewModel.setBarsShown(toolbarBarShow)
        }
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
        initToolbar()
        initDocument()
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
        pdfView.useBestQuality(true)
        pdfView.fromUri(document.path)
            .defaultPage(document.currentPage)
            .onPageChange { page, pageCount ->
                toolbar.title = getString(R.string.page) + " ${page + 1}/$pageCount"
                currentPage = page
            }
            .onTap {
                if (it.action == MotionEvent.ACTION_DOWN) {
                    fileViewModel.setBarsShown(!toolbarBarShow)
                    (activity as MainActivity).systemUiState(!toolbarBarShow)
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
            .pageFling(Prefs.getBoolean(AUTO_SPACING, true))
            .nightMode(Prefs.getBoolean(NIGHT_MODE, false))
            .pageSnap(Prefs.getBoolean(AUTO_SPACING, true))
            .linkHandler(CustomLinkHandler(pdfView))
            .scrollHandle(CustomScrollHandler(requireActivity().applicationContext, false))
            .load()
    }

    private fun initToolbar() {
        toolbar.inflateMenu(R.menu.file_toolbar_menu)
        if (Prefs.getBoolean(NIGHT_MODE, false))
            toolbar.menu.findItem(R.id.day_night).icon =
                ContextCompat.getDrawable(pdfView.context, R.drawable.ic_light_mode)
        else
            toolbar.menu.findItem(R.id.day_night).icon =
                ContextCompat.getDrawable(pdfView.context, R.drawable.ic_dark_mode)
        if (Prefs.getBoolean(AUTO_SPACING, false))
            toolbar.menu.findItem(R.id.page_break).icon =
                ContextCompat.getDrawable(pdfView.context, R.drawable.ic_page_break)
        else
            toolbar.menu.findItem(R.id.page_break).icon =
                ContextCompat.getDrawable(pdfView.context, R.drawable.ic_page_by_page)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.day_night -> {
                    val night_mode = !Prefs.getBoolean(NIGHT_MODE, false)
                    pdfView.setNightMode(night_mode)
                    pdfView.loadPages()
                    Prefs.putBoolean(NIGHT_MODE, night_mode)
                    if (night_mode)
                        toolbar.menu.findItem(R.id.day_night).icon =
                            ContextCompat.getDrawable(pdfView.context, R.drawable.ic_light_mode)
                    else
                        toolbar.menu.findItem(R.id.day_night).icon =
                            ContextCompat.getDrawable(pdfView.context, R.drawable.ic_dark_mode)
                    true
                }
                R.id.page_break -> {
                    val auto_spacing = !Prefs.getBoolean(AUTO_SPACING, false)
                    Prefs.putBoolean(AUTO_SPACING, auto_spacing)
                    if (auto_spacing)
                        toolbar.menu.findItem(R.id.page_break).icon =
                            ContextCompat.getDrawable(pdfView.context, R.drawable.ic_page_break)
                    else
                        toolbar.menu.findItem(R.id.page_break).icon =
                            ContextCompat.getDrawable(pdfView.context, R.drawable.ic_page_by_page)
                    document.currentPage = currentPage
                    fileViewModel.saveFile(document)
                    loadPdf(document)
                    true
                }
                else -> false
            }
        }
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).navController.popBackStack()
        }
    }

    override fun onStop() {
        super.onStop()
        document.currentPage = currentPage
        document.date_modified = System.currentTimeMillis()
        fileViewModel.saveFile(document)
        pdfView.recycle()
    }

}