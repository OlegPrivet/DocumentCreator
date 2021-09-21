package com.olegdev.documentcreator.fragments.homefragments.pdfsfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.viewmodels.FileViewModel
import java.util.*

class PdfsFragment : Fragment() {

    private val TAG = PdfsFragment::class.simpleName

    private lateinit var fileViewModel: FileViewModel
    private lateinit var document: Document
    private lateinit var docId: UUID
    private var currentPage = 0
    private lateinit var pdfView: PDFView
    private val args by navArgs<PdfsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(FileViewModel::class.java)
        docId = args.docId
        fileViewModel.loadFile(docId)
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
        initDocument()
    }

    private fun initDocument() {
        fileViewModel.fileLiveData.observe(viewLifecycleOwner, { document ->
            document?.let {
                this.document = document
                loadPdf(document)
            }
        })
    }

    private fun loadPdf(document: Document) {
        currentPage = document.currentPage
        pdfView.fromUri(document.path)
            .enableSwipe(true)
            .defaultPage(currentPage)
            .onPageChange { page, pageCount ->
                currentPage = page
            }
            .swipeHorizontal(true)
            .enableDoubletap(true)
            .spacing(0)
            .autoSpacing(true)
            .pageFitPolicy(FitPolicy.WIDTH)
            .fitEachPage(true)
            .pageSnap(true)
            .pageFling(true)
            .load()
    }

    override fun onStop() {
        super.onStop()
        document.currentPage = currentPage
        document.date_modified = System.currentTimeMillis()
        Log.e(TAG, "document - $document")
        fileViewModel.saveFile(document)
    }
}