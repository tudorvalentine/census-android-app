package com.example.censusapp

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.ui.PdfActivity

class PDFViewerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = Uri.parse("file:///android_asset/doc.pdf")
        val config = PdfActivityConfiguration.Builder(applicationContext).build()
        PdfActivity.showDocument(this, uri, config)
    }
}
