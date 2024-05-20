package com.anggaa.projectpricetag.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggaa.projectpricetag.R
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import com.anggaa.projectpricetag.data.AppDatabase
import com.anggaa.projectpricetag.data.entity.Pricetagnormal
import com.anggaa.projectpricetag.view.adapter.DetailProdukAdapter

class DetailProduk : AppCompatActivity() {

    private lateinit var SearchView: SearchView
    private lateinit var RecyclerView: RecyclerView
    private lateinit var adapter: DetailProdukAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_produk)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews(){

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }

        SearchView = findViewById(R.id.SearchBar)
        RecyclerView = findViewById(R.id.RecyclerView)

        SearchView.queryHint = "Cari produk..."

        val database = AppDatabase.getInstance(this)

        val itemList = database.PricetagnormalDAO().getAll()

        RecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DetailProdukAdapter(this, itemList.sortedBy { it.Nama }.toMutableList())
        RecyclerView.adapter = adapter

        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""
                adapter.filter(query)
                return true
            }
        })

        val CardViewUrutkan = findViewById<CardView>(R.id.CardUrutkan)
        CardViewUrutkan.setOnClickListener {
            ShowDialogUrutkan(itemList.toMutableList(), adapter)
        }

        val CardTambah = findViewById<CardView>(R.id.CardTambah)
        CardTambah.setOnClickListener {
            val database = AppDatabase.getInstance(this)

            val dialog_edit = Dialog(this)
            dialog_edit.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog_edit.setContentView(R.layout.dialog_edit_produk)
            dialog_edit.setCancelable(true)
            dialog_edit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val edtNamaProduk = dialog_edit.findViewById<EditText>(R.id.EDTNamaProduk)
            val edtBarcodeProduk = dialog_edit.findViewById<EditText>(R.id.EDTBarcodeProduk)
            val edtKategoriProduk = dialog_edit.findViewById<EditText>(R.id.EDTKategoriProduk)
            val edtSupplierProduk = dialog_edit.findViewById<EditText>(R.id.EDTSupplierProduk)
            val edtBrandProduk = dialog_edit.findViewById<EditText>(R.id.EDTBrandProduk)

            dialog_edit.findViewById<Button>(R.id.ButtonBatal).setOnClickListener {
                dialog_edit.dismiss()
            }

            dialog_edit.findViewById<Button>(R.id.ButtonKonfirmasi).setOnClickListener {

                val data = Pricetagnormal(
                    Nama = edtNamaProduk.text.toString(),
                    Barcode = edtBarcodeProduk.text.toString(),
                    Kategori = edtKategoriProduk.text.toString(),
                    Supplier = edtSupplierProduk.text.toString(),
                    Brand = edtBrandProduk.text.toString(),
                    UOM = "Pcs",
                    Harga = "0",
                    TGC = "",
                    Returan = "0",
                    AlamatRak = "0"
                )

                database.PricetagnormalDAO().insert(
                    data
                )
                dialog_edit.dismiss()
                val intent = Intent(this, DetailProduk::class.java)
                startActivity(intent)
                finish()
            }

            dialog_edit.show()
        }

    }

    private fun ShowDialogUrutkan(list: MutableList<Pricetagnormal>, adapter: DetailProdukAdapter) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_urutkan)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val checkboxAZ = dialog.findViewById<CheckBox>(R.id.checkBoxAZ)
        val checkboxZA = dialog.findViewById<CheckBox>(R.id.checkBoxZA)

        checkboxAZ.isChecked = true

        checkboxAZ.setOnClickListener {
            if (checkboxAZ.isChecked) {
                checkboxZA.isChecked = false
                val sortedList = list.sortedBy { it.Nama }
                adapter.updateItems(sortedList)
                Toast.makeText(this, "Urutkan dari A-Z", Toast.LENGTH_SHORT).show()
            }
        }

        checkboxZA.setOnClickListener {
            if (checkboxZA.isChecked) {
                checkboxAZ.isChecked = false
                val sortedList = list.sortedByDescending { it.Nama }
                adapter.updateItems(sortedList)
                Toast.makeText(this, "Urutkan dari Z-A", Toast.LENGTH_SHORT).show()
            }
        }

    }



}