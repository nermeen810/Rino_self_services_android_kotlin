package com.rino.self_services.ui.pdfViewer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class MyFile (val file: File): Parcelable