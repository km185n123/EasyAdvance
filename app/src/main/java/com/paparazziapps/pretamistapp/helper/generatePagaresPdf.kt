import android.content.Context
import android.graphics.Bitmap
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.ByteArrayOutputStream
import java.io.File

fun generatePagaresPdfWithSignature(
    context: Context,
    fileName: String,
    deudor: String,
    acreedor: String,
    importe: String,
    fechaEmision: String,
    fechaVencimiento: String,
    place: String,
    address: String,
    signature: Bitmap
): File {
    val pdfPath = File(context.getExternalFilesDir(null), fileName)
    val writer = PdfWriter(pdfPath)
    val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(writer)
    val document = Document(pdfDoc, PageSize.A4)

    // Contenido del pagaré
    val content = """
         Fecha de Emisión: $fechaEmision
        Lugar de Emisión: $place
        Importe: ${'$'}${importe.toString()} (Mil Pesos 00/100 M.N.)
        
        Vencimiento: $fechaVencimiento
        
        Deudor:
        Nombre: $deudor
        
        Acreedor:
        Nombre: $acreedor
        Domicilio: $address
        
        Por el presente documento, yo, $deudor, con domicilio en [Domicilio del Deudor], me comprometo a pagar incondicionalmente a $acreedor, con domicilio en [Domicilio del Acreedor], la cantidad de ${'$'}${importe.toString()} (Mil Pesos 00/100 M.N.) el día $fechaVencimiento.
        
        En caso de incumplimiento en la fecha de vencimiento, el Deudor se compromete a pagar un interés moratorio del [tasa de interés]% mensual sobre el saldo adeudado hasta la fecha de pago total.
        
        El presente pagaré se encuentra sujeto a las leyes y jurisdicción de [Ciudad, Estado].
        
        Firma del Deudor:  
        ________________________  
        $deudor
        
        Firma del Acreedor:  
         $acreedor
    """.trimIndent()

    // Crear un párrafo con el contenido del pagaré
    val paragraph = Paragraph(content)
        .setFontSize(12f)
        .setFontColor(ColorConstants.BLACK)
        .setTextAlignment(TextAlignment.JUSTIFIED)

    // Agregar el contenido al documento
    document.add(paragraph)

    // Convertir la firma del acreedor en imagen
    val byteArrayOutputStream = ByteArrayOutputStream()
    signature.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val signatureImage = Image(ImageDataFactory.create(byteArrayOutputStream.toByteArray()))
    signatureImage.scaleToFit(200f, 200f) // Escalar la imagen a 40x60 píxeles

    // Añadir la firma del acreedor al final del documento
    document.add(signatureImage)

    document.close()

    return pdfPath
}
