package com.anggaa.projectpricetag

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anggaa.projectpricetag.data.AppDatabase
import com.anggaa.projectpricetag.model.data
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.EAN13Writer
import com.itextpdf.kernel.geom.PageSize
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val PDF_PAGE_WIDTH = 595
    private val PDF_PAGE_HEIGHT = 842
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var ListData: MutableList<data>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.buttonDocument).setOnClickListener {
            makePDFInBackground(8, 4)
        }
    }

    private fun makePDFInBackground(rowCount: Int, columnCount: Int) {
        // Buat dan jalankan Thread baru
        Thread {
            // Panggil fungsi makePDF di dalam Thread
            makePDF(rowCount, columnCount)
        }.start()
    }

    private fun makePDF(rowCount: Int, columnCount: Int) {
        // Membuat layout yang akan menampung semua LinearLayout
        val parentLayout = LinearLayout(this)
        parentLayout.orientation = LinearLayout.VERTICAL

        // Memanggil fungsi untuk membuat grid TextView dan Button
        addGridToLayout(parentLayout, rowCount, columnCount)

        // Membuat PDF dengan layout yang telah dibuat
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Ukuran A4 (595 x 842 piksel)
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        parentLayout.measure(595, 842)
        parentLayout.layout(0, 0, 595, 842)
        parentLayout.draw(canvas)

        pdfDocument.finishPage(page)

        // Menyimpan PDF ke dalam perangkat
        val file = File(filesDir, "${System.currentTimeMillis()}.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        // Menampilkan pesan toast dengan lokasi PDF disimpan
        val toastMessage = "PDF disimpan di: ${file.absolutePath}"
        logData("PDF Disimpan", toastMessage)
        showToast(toastMessage)

        // Membuka PDF secara langsung setelah disimpan
        openPDF(file)
    }

    private fun openPDF(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    private fun addGridToLayout(layout: LinearLayout, rows: Int, columns: Int) {
        // Hitung ukuran setiap sel
        val cellWidth = 595 / columns // Lebar layout A4 dibagi dengan jumlah kolom
        val cellHeight = 842 / rows // Tinggi layout A4 dibagi dengan jumlah baris

        // Melakukan iterasi untuk membuat grid dengan TextView dan Button
        for (row in 0 until rows) {
            // Membuat LinearLayout untuk setiap baris
            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Melakukan iterasi untuk membuat LinearLayout dalam satu baris
            for (column in 0 until columns) {
                // Memanggil fungsi untuk menambahkan TextView dan Button ke dalam LinearLayout
//                val cellLayout = createCellLayout(row * columns + column + 1, layout.context)
                val cellLayout = createCellLayoutFormXML(row * columns + column + 1, layout.context)
                val layoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
                cellLayout.layoutParams = layoutParams
                rowLayout.addView(cellLayout)
            }

            // Menambahkan LinearLayout baris ke dalam layout utama
            layout.addView(rowLayout)
        }
    }

    private fun createCellLayoutFormXML(index: Int, context: Context): LinearLayout {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cellLayout = inflater.inflate(R.layout.cell_layout_promo, null) as LinearLayout
        cellLayout.findViewById<ImageView>(R.id.barcode).setImageBitmap(
            generateBarcode("123456789012")
        )

        cellLayout.findViewById<TextView>(R.id.HargaNormal).paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        cellLayout.findViewById<TextView>(R.id.HargaNormal).text = "Rp. 100.000"


        return cellLayout
    }

    fun generateBarcode(id: String): Bitmap? {
        val writer = EAN13Writer()
        try {
            val bitMatrix: BitMatrix = writer.encode(id, BarcodeFormat.EAN_13, 1800, 300)
            val width: Int = bitMatrix.width
            val height: Int = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.WHITE else Color.RED)
                }
            }
            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createCellLayout(index: Int, context: Context): LinearLayout {
        // Membuat LinearLayout dan menambahkan TextView dan Button ke dalamnya
        val cellLayout = LinearLayout(context)
        cellLayout.orientation = LinearLayout.VERTICAL
        cellLayout.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        val textView = TextView(context)
        textView.text = "TextView $index"
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val button = Button(context)
        button.text = "Button $index"
        button.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cellLayout.addView(textView)
        cellLayout.addView(button)

        return cellLayout
    }


    private fun getData() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val dbRef = FirebaseDatabase.getInstance().getReference("produk")
        val batchSize = 100 // Jumlah data dalam satu batch
        var batchCounter = 0
        var totalDataCount = 0 // Variabel untuk menghitung jumlah total data

        ListData = ArrayList()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snap in snapshot.children) {
                    val data = snap.getValue(data::class.java)
                    logData("Data", data?.name.toString())
                    ListData.add(data!!)
                    logData("Data Size", ListData.size.toString())
                }

                totalDataCount += snapshot.childrenCount.toInt()

                // Periksa apakah masih ada data
                if (snapshot.childrenCount >= batchSize) {
                    // Ambil data selanjutnya
                    val lastData = snapshot.children.last()
                    val lastId = lastData.key
                    dbRef.orderByKey().startAt(lastId).limitToFirst(batchSize).addListenerForSingleValueEvent(this)
                } else {
                    batchCounter++

                    // Periksa apakah semua batch sudah selesai
                    if (ListData.size == 7085) {
                        dialog.dismiss()
                        Toast.makeText(this@MainActivity, "Data Loaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(snapshot: DatabaseError) {
                dialog.dismiss()
                logData("Error", snapshot.message)
            }
        }

        // Mulai dengan mengambil data pertama kali
        dbRef.orderByKey().limitToFirst(batchSize).addListenerForSingleValueEvent(listener)
    }

    private fun logData(tag: String, data: String) {
        Log.d(tag, data)
    }
}