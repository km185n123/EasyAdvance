package com.paparazziapps.pretamistapp.helper

import android.content.Context
import android.graphics.Bitmap
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
fun createPdfWithSignature(context: Context, signature: Bitmap): File {
    val pdfPath = File(context.getExternalFilesDir(null), "pagare.pdf")
    val pdfWriter = PdfWriter(FileOutputStream(pdfPath))
    val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
    val document = Document(pdfDocument)

    // Añadir contenido al PDF
    document.add(Paragraph("Pagaré de 1000 pesos"))

    // Convertir la firma en imagen
    val byteArrayOutputStream = ByteArrayOutputStream()
    signature.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val signatureImage = Image(ImageDataFactory.create(byteArrayOutputStream.toByteArray()))

    // Añadir la imagen de la firma al PDF
    document.add(signatureImage)

    document.close()
    return pdfPath
}