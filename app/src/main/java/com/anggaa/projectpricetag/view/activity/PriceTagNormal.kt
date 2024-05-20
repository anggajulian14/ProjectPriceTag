package com.anggaa.projectpricetag.view.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.data.AppDatabase
import com.anggaa.projectpricetag.data.dao.BrandDAO
import com.anggaa.projectpricetag.data.dao.KategoriDAO
import com.anggaa.projectpricetag.data.dao.SupplierDAO
import com.anggaa.projectpricetag.data.dao.UOMDAO
import com.anggaa.projectpricetag.data.entity.Pricetagnormal
import com.anggaa.projectpricetag.model.DecimalTextWatcher
import com.anggaa.projectpricetag.model.PricetagNormal
import com.anggaa.projectpricetag.model.SharedPreferencesManager
import com.anggaa.projectpricetag.view.adapter.CustomSpinnerAdapter
import com.anggaa.projectpricetag.view.adapter.DataProdukAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.oned.EAN13Writer
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class PriceTagNormal : AppCompatActivity() {

    private lateinit var fabScanner: FloatingActionButton
    private lateinit var database: AppDatabase
    private lateinit var ACTV_NamaProduk: AutoCompleteTextView
    private lateinit var ACTV_Barcode: AutoCompleteTextView
    private lateinit var ACTV_Kategori: AutoCompleteTextView
    private lateinit var ACTV_Supplier: AutoCompleteTextView
    private lateinit var ACTV_Brand: AutoCompleteTextView
    private lateinit var ACTV_UOM: AutoCompleteTextView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listKey: String

    private lateinit var fabPrint: FloatingActionButton

    private lateinit var adapterKategori: CustomSpinnerAdapter
    private lateinit var spinnerKategori: Spinner
    private lateinit var adapterSupllier: CustomSpinnerAdapter
    private lateinit var spinnerSupplier: Spinner
    private lateinit var adapterBrand: CustomSpinnerAdapter
    private lateinit var spinnerBrand: Spinner

    private lateinit var adapterUOM: CustomSpinnerAdapter
    private lateinit var spinnerUOM: Spinner

    private var listProduk: MutableList<PricetagNormal> = mutableListOf()

    private var itemList: MutableList<String> = mutableListOf()
    private var listSupplier: MutableList<String> = mutableListOf()
    private var listUOM: MutableList<String> = mutableListOf()
    private var listBrand: MutableList<String> = mutableListOf()

    private lateinit var listNamaProduk: MutableList<String>
    private lateinit var listBarcode: MutableList<String>
    private var filteredNamaProduk: MutableList<String> = mutableListOf()

    private lateinit var SelectedNama: String
    private lateinit var SelectedKategori: String
    private lateinit var SelectedSupplier: String
    private lateinit var SelectedBardcode: String
    private lateinit var SelectedBrand: String
    private lateinit var SelectedUOM: String
    private lateinit var Harga: String
    private lateinit var TGC: String
    private lateinit var Returan: String
    private lateinit var AlamatRak: String

    private lateinit var R1: EditText
    private lateinit var R2: EditText
    private lateinit var R3: EditText
    private lateinit var R4: EditText

    private lateinit var RDP1: EditText
    private lateinit var RDP2: EditText

    private lateinit var RDD1: EditText
    private lateinit var RDD2: EditText
    private lateinit var RDD3: EditText

    private lateinit var RKSR11: EditText
    private lateinit var RKSR12: EditText

    private lateinit var RKSR21: EditText
    private lateinit var RKSR22: EditText

    private lateinit var AlamatRak_R: CheckBox
    private lateinit var AlamatRak_RDP: CheckBox
    private lateinit var AlamatRak_RDD: CheckBox
    private lateinit var AlamatRak_RKSRBL: CheckBox
    private lateinit var AlamatRak_RKSRDP: CheckBox

    private lateinit var hargaBarangEditText: EditText
    private lateinit var tgcEditText: EditText

    private lateinit var EDTRTBEX: EditText
    private lateinit var EDTNRTBEX: EditText

    private lateinit var returan_NRTBEX: CheckBox
    private lateinit var returan_RTBEX: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price_tag_normal)

        sharedPreferences = getSharedPreferences("ProductPreferences", Context.MODE_PRIVATE)
        listKey = "productList"

        initView()
    }

    private fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getInstance(this)

        val ListSupplierRaw = database.PricetagnormalDAO().getAll().map { it.Supplier }.toMutableList()

        val listSupplierWithoutDuplicates = ListSupplierRaw.distinct()

        val ListSupplier = listSupplierWithoutDuplicates.filterNotNull() as MutableList<String>

        val buttonScan = findViewById<CardView>(R.id.ButtonCari)
        buttonScan.setOnClickListener {
            scanCode()
        }

        val ListKategoriRaw = database.PricetagnormalDAO().getAll().map { it.Kategori }.toMutableList()
        val listKategoriWithoutDuplicates = ListKategoriRaw.distinct()

        val ListBrandRaw = database.PricetagnormalDAO().getAll().map { it.Brand }.toMutableList()
        val listBrandWithoutDuplicates = ListBrandRaw.distinct()

        val ListUOMRaw = database.PricetagnormalDAO().getAll().map { it.UOM }.toMutableList()
        val listUOMWithoutDuplicates = ListUOMRaw.distinct()

        listNamaProduk = database.PricetagnormalDAO().getAll().map { it.Nama }.toMutableList()
        listBarcode = database.PricetagnormalDAO().getAll().map { it.Barcode }.toMutableList()
        val ListKategori = listKategoriWithoutDuplicates.filterNotNull() as MutableList<String>
        val ListBrand = listBrandWithoutDuplicates.filterNotNull() as MutableList<String>
        val ListUOM = listUOMWithoutDuplicates.filterNotNull() as MutableList<String>

        logData("List Kategori", ListKategori.toString())
        logData("List Brand", ListBrand.toString())
        logData("List UOM", ListUOM.toString())

        ACTV_NamaProduk = findViewById(R.id.SearchView)
        ACTV_Barcode = findViewById(R.id.spinnerSKU)
        ACTV_Kategori = findViewById(R.id.SpinnerKategori)
        ACTV_Supplier = findViewById(R.id.SpinnerSupplier)
        ACTV_Brand = findViewById(R.id.SpinnerBrand)
        ACTV_UOM = findViewById(R.id.SpinnerUOM)

        val adapterNama = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listNamaProduk)

        ACTV_NamaProduk.setAdapter(adapterNama)
        ACTV_NamaProduk.threshold = 1

        ACTV_NamaProduk.setOnItemClickListener { parent, view, position, id ->
            ACTV_Barcode.setText(database.PricetagnormalDAO().getName(adapterNama.getItem(position)!!).Barcode)
            ACTV_Supplier.setText(database.PricetagnormalDAO().getName(adapterNama.getItem(position)!!).Supplier)

            Toast.makeText(this, "Barcode: ${ACTV_Barcode.text.toString()}", Toast.LENGTH_SHORT).show()

            val pricetag = database.PricetagnormalDAO().getByBarcode(ACTV_Barcode.text.toString())

            if (pricetag != null) {
                ACTV_Kategori.setText(pricetag.Kategori)
                ACTV_Brand.setText(pricetag.Brand)
                ACTV_UOM.setText(pricetag.UOM)
            }

        }

        val adapterKategori = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ListKategori)

        ACTV_Kategori.setAdapter(adapterKategori)
        ACTV_Kategori.threshold = 1

        ACTV_Kategori.setOnItemClickListener { parent, view, position, id ->
            ACTV_Kategori.setText(adapterKategori.getItem(position)!!)
        }

        val adapterBarcode = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listBarcode)

        ACTV_Barcode.setAdapter(adapterBarcode)
        ACTV_Barcode.threshold = 1

        ACTV_Barcode.setOnItemClickListener { parent, view, position, id ->
            ACTV_Barcode.setText(adapterBarcode.getItem(position)!!)
        }

        val adapterSupplier = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ListSupplier)

        ACTV_Supplier.setAdapter(adapterSupplier)
        ACTV_Supplier.threshold = 1

        ACTV_Supplier.setOnItemClickListener { parent, view, position, id ->
            ACTV_Supplier.setText(adapterSupplier.getItem(position)!!)
        }

        val adapterBrand = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ListBrand)

        ACTV_Brand.setAdapter(adapterBrand)
        ACTV_Brand.threshold = 1

        ACTV_Brand.setOnItemClickListener { parent, view, position, id ->
            ACTV_Brand.setText(adapterBrand.getItem(position)!!)
        }

        val adapterUOM = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ListUOM)

        ACTV_UOM.setAdapter(adapterUOM)
        ACTV_UOM.threshold = 1

        ACTV_UOM.setOnItemClickListener { parent, view, position, id ->
            ACTV_UOM.setText(adapterUOM.getItem(position)!!)
        }

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentDate = LocalDate.now().format(dateFormatter)
        val currentTime = LocalTime.now().format(timeFormatter)
        val formattedDateTime = "$currentDate-$currentTime"

        val EDTHarga = findViewById<EditText>(R.id.HargaBarang)
        EDTHarga.addTextChangedListener(DecimalTextWatcher(EDTHarga))

        findViewById<EditText>(R.id.TGC).setText("$formattedDateTime")

        returan_RTBEX = findViewById<CheckBox>(R.id.CheckboxRT)
        returan_NRTBEX = findViewById<CheckBox>(R.id.CheckboxNRT)

        Returan = ""
        EDTRTBEX = findViewById<EditText>(R.id.EDTRTBEX)
        EDTNRTBEX = findViewById<EditText>(R.id.EDTNRTBEX)

        returan_RTBEX.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                returan_NRTBEX.isChecked = false}
        }

        returan_NRTBEX.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                returan_RTBEX.isChecked = false}
        }

        AlamatRak_R = findViewById<CheckBox>(R.id.CheckboxR)
        AlamatRak_RDP = findViewById<CheckBox>(R.id.CheckboxRDP)
        AlamatRak_RDD = findViewById<CheckBox>(R.id.CheckboxRDD)
        AlamatRak_RKSRBL = findViewById<CheckBox>(R.id.CheckboxRKSR1)
        AlamatRak_RKSRDP = findViewById<CheckBox>(R.id.CheckboxRKSR2)

        AlamatRak = ""

        R1 = findViewById<EditText>(R.id.R1)
        R2 = findViewById<EditText>(R.id.R2)
        R3 = findViewById<EditText>(R.id.R3)
        R4 = findViewById<EditText>(R.id.R4)

        RDP1 = findViewById<EditText>(R.id.RDP1)
        RDP2 = findViewById<EditText>(R.id.RDP2)

        RDD1 = findViewById<EditText>(R.id.RDD1)
        RDD2 = findViewById<EditText>(R.id.RDD2)
        RDD3 = findViewById<EditText>(R.id.RDD3)

        RKSR11 = findViewById<EditText>(R.id.RKSR11)
        RKSR12 = findViewById<EditText>(R.id.RKSR12)

        RKSR21 = findViewById<EditText>(R.id.RKSR21)
        RKSR22 = findViewById<EditText>(R.id.RKSR22)

        AlamatRak_R.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlamatRak_RDP.isChecked = false
                AlamatRak_RDD.isChecked = false
                AlamatRak_RKSRBL.isChecked = false
                AlamatRak_RKSRDP.isChecked = false
            }
        }

        AlamatRak_RDP.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlamatRak_R.isChecked = false
                AlamatRak_RDD.isChecked = false
                AlamatRak_RKSRBL.isChecked = false
                AlamatRak_RKSRDP.isChecked = false
                AlamatRak ="RDP:G${RDP1.text}:T${RDP2.text}DB"
            }
        }

        AlamatRak_RDD.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlamatRak_R.isChecked = false
                AlamatRak_RDP.isChecked = false
                AlamatRak_RKSRBL.isChecked = false
                AlamatRak_RKSRDP.isChecked = false
            }
        }

        AlamatRak_RKSRBL.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlamatRak_R.isChecked = false
                AlamatRak_RDP.isChecked = false
                AlamatRak_RDD.isChecked = false
                AlamatRak_RKSRDP.isChecked = false
            }
        }

        AlamatRak_RKSRDP.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlamatRak_R.isChecked = false
                AlamatRak_RDP.isChecked = false
                AlamatRak_RDD.isChecked = false
                AlamatRak_RKSRBL.isChecked = false
            }
        }

        hargaBarangEditText = findViewById<EditText>(R.id.HargaBarang)

        fabPrint = findViewById(R.id.fabPrint)
        fabPrint.setOnClickListener {
            showDialogCheck()
        }

        val buttonSubmit = findViewById<Button>(R.id.ButtonTambah)

        buttonSubmit.setOnClickListener {
            hargaBarangEditText = findViewById<EditText>(R.id.HargaBarang)
            tgcEditText = findViewById<EditText>(R.id.TGC)

            val hargaBarang = hargaBarangEditText.text.toString()
            val tgc = tgcEditText.text.toString()

            SelectedNama = ACTV_NamaProduk.text.toString()
            SelectedBardcode = ACTV_Barcode.text.toString()
            SelectedSupplier = ACTV_Supplier.text.toString()
            SelectedKategori = ACTV_Kategori.text.toString()
            SelectedBrand = ACTV_Brand.text.toString()
            SelectedUOM = ACTV_UOM.text.toString()

            Returan = if (returan_RTBEX.isChecked) {
                "RT-BEX-${EDTRTBEX.text}/TR-RTS-AR"
            } else {
                "NRT-BEX-${EDTNRTBEX.text}/TR-RPRO-MA"
            }

            AlamatRak = when {
                AlamatRak_R.isChecked -> "R${R1.text}:M${R2.text}:G${R3.text}:T${R4.text}DB"
                AlamatRak_RDP.isChecked -> "RDP:G${RDP1.text}:T${RDP2.text}:DB"
                AlamatRak_RDD.isChecked -> "RDD:${RDD1.text}:G${RDD2.text}:T${RDD3.text}:DB"
                AlamatRak_RKSRBL.isChecked -> "RKSR:BL:E${RKSR11.text}:T${RKSR12.text}:DB"
                AlamatRak_RKSRDP.isChecked -> "RKSR:DP:K${RKSR21.text}:T${RKSR22.text}:DB"
                else -> "" // default jika tidak ada yang dipilih
            }

            if (SelectedNama.isNotBlank() &&
                SelectedSupplier.isNotBlank() &&
                SelectedSupplier != "Pilih Supllier" &&
                SelectedKategori.isNotBlank() &&
                SelectedKategori != "Pilih Kategori" &&
                SelectedBrand.isNotBlank() &&
                SelectedBrand != "Pilih Brand" &&
                SelectedBardcode.isNotBlank() &&
                SelectedUOM.isNotBlank() &&
                findViewById<EditText>(R.id.HargaBarang).text.toString().isNotBlank()&&
                findViewById<EditText>(R.id.TGC).text.toString().isNotBlank()&&
                Returan.isNotBlank() &&
                AlamatRak.isNotBlank()) {
                val pricetag = PricetagNormal(
                    SelectedNama,
                    SelectedBardcode,
                    SelectedKategori,
                    SelectedSupplier,
                    SelectedBrand,
                    SelectedUOM,
                    findViewById<EditText>(R.id.HargaBarang).text.toString(),
                    findViewById<EditText>(R.id.TGC).text.toString(),
                    Returan,
                    AlamatRak
                )
                if (listProduk.size > 31) {
                    Toast.makeText(this, "Produk melebihi batas", Toast.LENGTH_SHORT).show()
                } else {
                    SharedPreferencesManager.saveProductItem(this, pricetag)
                    logData("Pricetag Normal", pricetag.toString())
                    showDialogCheck()
                    //check if data is in database
                    val check = database.PricetagnormalDAO().getByBarcode(SelectedBardcode)
                    if (check == null) {
                        val PriceTag = Pricetagnormal(
                            Nama = pricetag.Nama,
                            Barcode = pricetag.Barcode,
                            Kategori = pricetag.Kategori,
                            Supplier = pricetag.Supplier,
                            Brand = pricetag.Brand,
                            UOM = pricetag.UOM,
                            Harga = pricetag.Harga,
                            TGC = pricetag.TGC,
                            Returan = pricetag.Returan,
                            AlamatRak = pricetag.AlamatRak
                        )
                        lifecycleScope.launch {
                            database.PricetagnormalDAO().insert(PriceTag)
                        }
                    } else {
                        database.PricetagnormalDAO().updateAll(SelectedBardcode, SelectedNama, SelectedKategori, SelectedSupplier, SelectedBrand)
                    }
                }
            } else {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                logData("Data",
                    "$SelectedNama, " +
                            "$SelectedBardcode, " +
                            "$SelectedSupplier, " +
                            "$SelectedKategori, " +
                            "$SelectedBrand, " +
                            "$SelectedUOM, " +
                            "${findViewById<EditText>(R.id.HargaBarang).text.toString()}, " +
                            "${findViewById<EditText>(R.id.TGC).text.toString()}, " +
                            " $Returan, " +
                            AlamatRak
                )
            }
        }

    }

    fun initializeAutocompleteTextView(autoCompleteTextView: AutoCompleteTextView, dataList: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dataList)
        var selectedValue: String? = null

        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            selectedValue = adapter.getItem(position) as String
            SelectedNama = selectedValue.toString()
            // Lakukan sesuatu dengan nilai yang dipilih
            Toast.makeText(this, "Selected: $selectedValue", Toast.LENGTH_SHORT).show()
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                selectedValue = null

                if (dataList.contains(inputText)) {
                    selectedValue = inputText
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun scanCode() {
        val options = ScanOptions().apply {
            setPrompt("Scan Barcode")
            setBeepEnabled(true)
            setOrientationLocked(true)
            captureActivity = ScannerActivity::class.java
        }
        barLauncher.launch(options)
    }

    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
        val database = AppDatabase.getInstance(this)
        if (result.contents != null) {
            val barcode = result.contents
            val produk = database.dataDAO().getByBarcode(barcode)
            val priceTagNormal = database.PricetagnormalDAO().getByBarcode(barcode)

            if (produk == null && priceTagNormal == null) {
                Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            findViewById<AutoCompleteTextView>(R.id.spinnerSKU)?.setText(produk.barcode ?: "")
            findViewById<AutoCompleteTextView>(R.id.SearchView)?.setText(produk.name ?: "")
            findViewById<AutoCompleteTextView>(R.id.SpinnerSupplier)?.setText(produk.supplier ?: "")
            findViewById<AutoCompleteTextView>(R.id.SpinnerKategori)?.setText(priceTagNormal?.Barcode ?: "")
            findViewById<AutoCompleteTextView>(R.id.SpinnerBrand)?.setText(priceTagNormal?.Brand ?: "")
            findViewById<AutoCompleteTextView>(R.id.SpinnerUOM)?.setText(priceTagNormal?.UOM ?: "")
        }
    }


    private fun makePDF(rowCount: Int, columnCount: Int, pricetagList: List<PricetagNormal>) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("PricetagNormal")
        fun getCurrentDateAsString(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd") // Tentukan format tanggal yang diinginkan di sini
            val currentDate = Date() // Dapatkan tanggal saat ini
            return dateFormat.format(currentDate) // Ubah tanggal menjadi string sesuai dengan format yang ditentukan
        }

        // Membuat dialog
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // Membuat layout yang akan menampung semua LinearLayout
        val parentLayout = LinearLayout(this)
        parentLayout.orientation = LinearLayout.VERTICAL

        // Memanggil fungsi untuk membuat grid TextView dan ImageView
        addGridToLayout(parentLayout, rowCount, columnCount, pricetagList)

        // Membuat PDF dengan layout yang telah dibuat
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Ukuran A4 (595 x 842 piksel)
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        parentLayout.measure(595, 842)
        parentLayout.layout(0, 0, 595, 842)
        parentLayout.draw(canvas)

        pdfDocument.finishPage(page)

        val time = System.currentTimeMillis()

        // Menyimpan PDF ke dalam perangkat
        val file = File(filesDir, "${time}.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        val TanggalHariini = databaseReference.child(getCurrentDateAsString())

        TanggalHariini.child(time.toString()).setValue(pricetagList).addOnCompleteListener {
            if (it.isSuccessful) {
                // Menampilkan pesan toast dengan lokasi PDF disimpan
                val toastMessage = "PDF disimpan di: ${file.absolutePath}"
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
                // Menutup dialog
                dialog.dismiss()

                // Membuka PDF secara langsung setelah disimpan
                openPDF(file)
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openPDF(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    private fun addGridToLayout(layout: LinearLayout, rows: Int, columns: Int, pricetagList: List<PricetagNormal>) {
        // Hitung ukuran setiap sel
        val cellWidth = 595 / columns // Lebar layout A4 dibagi dengan jumlah kolom
        val cellHeight = 842 / rows // Tinggi layout A4 dibagi dengan jumlah baris

        // Melakukan iterasi untuk membuat grid dengan TextView dan ImageView
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
                // Memanggil fungsi untuk menambahkan TextView dan ImageView ke dalam LinearLayout
                val index = row * columns + column
                val cellLayout = if (index < pricetagList.size) {
                    createCellLayoutFormXML(pricetagList[index], layout.context)
                } else {
                    // Lakukan penanganan jika indeks di luar batas ukuran list
                    // Misalnya, jika tidak ada objek yang sesuai dengan indeks yang diinginkan
                    createCellLayoutNull(null, layout.context) // Jangan lupa menyesuaikan dengan tipe objek yang diharapkan
                }
                val layoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
                cellLayout.layoutParams = layoutParams
                rowLayout.addView(cellLayout)
            }

            // Menambahkan LinearLayout baris ke dalam layout utama
            layout.addView(rowLayout)
        }
    }

    private fun createCellLayoutNull(pricetag: PricetagNormal?, context: Context): LinearLayout {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cellLayout = inflater.inflate(R.layout.cell_kosong, null) as LinearLayout

        return cellLayout
    }

    private fun createCellLayoutFormXML(pricetag: PricetagNormal?, context: Context): LinearLayout {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cellLayout = inflater.inflate(R.layout.cell_layout, null) as LinearLayout

        // Set data barcode
        cellLayout.findViewById<ImageView>(R.id.barcode).setImageBitmap(
            generateBarcode(pricetag?.Barcode ?: "")
        )

        val Name = cellLayout.findViewById<TextView>(R.id.NamaBarang)
        Name.text = pricetag?.Nama

        val BarcodeNumber = cellLayout.findViewById<TextView>(R.id.barcodeNumber)
        BarcodeNumber.text = pricetag?.Barcode

        val Detail = cellLayout.findViewById<TextView>(R.id.DetailBarang)
        Detail.text = "${pricetag?.Kategori?.toUpperCase()}-${pricetag?.Supplier?.toUpperCase()}-${pricetag?.Brand?.toUpperCase()}"


        val Harga = cellLayout.findViewById<TextView>(R.id.HargaBarang)
        Harga.text = pricetag?.Harga

        val UOM = cellLayout.findViewById<TextView>(R.id.UOM)
        UOM.text = "/${pricetag?.UOM}"

        val TGC = cellLayout.findViewById<TextView>(R.id.TGC)
        TGC.text = pricetag?.TGC

        val Returan = cellLayout.findViewById<TextView>(R.id.Returan)
        Returan.text = pricetag?.Returan

        val AlamatRak = cellLayout.findViewById<TextView>(R.id.AlamatRak)
        AlamatRak.text = pricetag?.AlamatRak

        // Mengembalikan tata letak sel yang telah diperbarui
        return cellLayout
    }

    private fun generateBarcode(id: String?): Bitmap? {
        if (id.isNullOrEmpty()) {
            // Jika id kosong atau null, tampilkan pesan kesalahan atau kembalikan gambar kosong
            Log.e("generateBarcode", "ID cannot be empty or null.")
            return null
        }
        val writer = Code128Writer()
        try {
            val bitMatrix: BitMatrix = writer.encode(id, BarcodeFormat.CODE_128, 1800, 300)
            val width: Int = bitMatrix.width
            val height: Int = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }


    private fun initializeSpinner(spinnerId: Int, item: MutableList<String>, onItemSelected: (position: Int) -> Unit) {
        findViewById<Spinner>(spinnerId).apply {
            val adapter = CustomSpinnerAdapter(context, item)
            this.adapter = adapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    onItemSelected(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun showDialogCheck() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val list = SharedPreferencesManager.getProductListNormal(this@PriceTagNormal).toMutableList()

        val DataProduk = dialog.findViewById<RecyclerView>(R.id.DataProduk)
        DataProduk.layoutManager = LinearLayoutManager(this)

        val jumlahData =dialog.findViewById<TextView>(R.id.JUMLAHDATAPRODUK)
        jumlahData.text = "Jumlah Data : " + list.size

        val adapter = DataProdukAdapter(this@PriceTagNormal, list, jumlahData)
        DataProduk.adapter = adapter

        val buttonPrint = dialog.findViewById<Button>(R.id.ButtonPrint)
        buttonPrint.setOnClickListener {
            if (list.size in 1..32) {
                makePDF(8, 4, list)
                SharedPreferencesManager.clearProductListNormal(this@PriceTagNormal)
            } else {
                Toast.makeText(this, "Jumlah data harus 32  ", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonTambahData = dialog.findViewById<Button>(R.id.ButtonTambahProduk)
        buttonTambahData.setOnClickListener {
            ACTV_NamaProduk.setText("")
            ACTV_Barcode.setText("")
            ACTV_Supplier.setText("")
            ACTV_Brand.setText("")
            ACTV_Kategori.setText("")
            ACTV_UOM.setText("")
            hargaBarangEditText.text = null
            EDTRTBEX.text = null
            EDTNRTBEX.text = null
            returan_NRTBEX.isChecked = false
            returan_RTBEX.isChecked = false
            R1.text = null
            R2.text = null
            R3.text = null
            R4.text = null

            RDP1.text = null
            RDP2.text = null

            RDD1.text = null
            RDD2.text = null
            RDD3.text = null

            RKSR11.text = null
            RKSR12.text = null

            RKSR21.text = null
            RKSR22.text = null

            AlamatRak_R.isChecked = false
            AlamatRak_RDP.isChecked = false
            AlamatRak_RDD.isChecked = false
            AlamatRak_RKSRBL.isChecked = false
            AlamatRak_RKSRDP.isChecked = false


            dialog.dismiss()
        }

        dialog.show()
    }



    // Fungsi untuk menampilkan dialog tambah data
    fun showDialogTambahData(
        title: String,
        message: String,
        dao: Any,
        itemList: MutableList<String>,
        adapter: ArrayAdapter<String>,
        spinner: Spinner
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Tambah") { dialog, which ->
            val newData = input.text.toString()
            // Tambahkan data baru ke dalam database
            when (dao) {
                is KategoriDAO -> {
                    dao.insert(
                        System.currentTimeMillis().toInt(),
                        newData
                    )
                    itemList.clear() // Bersihkan itemList
                    itemList.add("Pilih $title")
                    itemList.add("Tambahkan $title")
                    itemList.addAll((dao as KategoriDAO).getAll().map { it.kategori }) // Ambil data dari database dan tambahkan ke itemList
                    spinner.setSelection(0)
                    adapter.notifyDataSetChanged() // Perbarui tampilan spinner
                }
                is SupplierDAO -> {
                    dao.insert(
                        System.currentTimeMillis().toInt(),
                        newData
                    )
                    itemList.clear() // Bersihkan itemList
                    itemList.add("Pilih $title")
                    itemList.add("Tambahkan $title")
                    itemList.addAll((dao as SupplierDAO).getAll().map { it.supplier }) // Ambil data dari database dan tambahkan ke itemList
                    spinner.setSelection(0)
                    adapter.notifyDataSetChanged() // Perbarui tampilan spinner
                }
                is BrandDAO -> {
                    dao.insert(
                        System.currentTimeMillis().toInt(),
                        newData
                    )
                    itemList.clear() // Bersihkan itemList
                    itemList.add("Pilih $title")
                    itemList.add("Tambahkan $title")
                    itemList.addAll((dao as BrandDAO).getAll().map { it.brand }) // Ambil data dari database dan tambahkan ke itemList
                    spinner.setSelection(0)
                    adapter.notifyDataSetChanged() // Perbarui tampilan spinner
                }
                is UOMDAO -> {
                    dao.insert(
                        System.currentTimeMillis().toInt(),
                        newData
                    )
                    itemList.clear() // Bersihkan itemList
                    itemList.add("Pilih $title")
                    itemList.add("Buyerkan $title")
                    itemList.addAll((dao as UOMDAO).getAll().map { it.uom }) // Ambil data dari database dan tambahkan ke itemList
                    spinner.setSelection(0)
                    adapter.notifyDataSetChanged() // Perbarui tampilan spinner
                }
                else -> {
                    // Handle other cases if needed
                }
            }
            Toast.makeText(this, "Data baru: $newData", Toast.LENGTH_SHORT).show()
        }


        builder.setNegativeButton("Batal") { dialog, which ->
            dialog.cancel()
            spinner.setSelection(0)
        }

        builder.show()
    }





    fun showContextMenuDialog(namaKategori: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Hapus Data")
        builder.setMessage("Anda yakin ingin menghapus data \"$namaKategori\"?")

        builder.setPositiveButton("Ya") { dialog, which ->
            Toast.makeText(this, "Data \"$namaKategori\" berhasil dihapus", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Tidak") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }


    private fun logData(tag: String, data: String){
        Log.d(tag, "$tag : $data")
    }
}