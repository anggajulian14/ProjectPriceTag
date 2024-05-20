package com.anggaa.projectpricetag.view.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.model.PricetagNormal
import com.anggaa.projectpricetag.model.PricetagPromo
import com.anggaa.projectpricetag.model.PrintLogLogPricetagPromo
import com.anggaa.projectpricetag.model.PrintLogPricetagNormal
import com.anggaa.projectpricetag.view.adapter.DetailProdukAdapter
import com.anggaa.projectpricetag.view.adapter.OnItemClickListener
import com.anggaa.projectpricetag.view.adapter.PrintLogItem
import com.anggaa.projectpricetag.view.adapter.RiwayatAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import kotlin.math.log

class RiwayatActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var RecyclerView: RecyclerView
    private lateinit var adapter: RiwayatAdapter
    private lateinit var dataNormal: List<PrintLogPricetagNormal>
    private lateinit var dataPromo: List<PrintLogLogPricetagPromo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dataNormal = emptyList()
        dataPromo = emptyList()

        initViews()
    }

    private fun initViews() {
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }

        val dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.dialog_loading)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        val dialog1 = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.dialog_loading)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        val dbrefNormal = FirebaseDatabase.getInstance().getReference("PricetagNormal")
        val dbrefPromo = FirebaseDatabase.getInstance().getReference("PricetagPromo")

        dbrefNormal.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val productList = handleSnapshot(snapshot)
                    dataNormal = productList
                    logData("Normal", snapshot.value.toString())
                    checkDataLoaded(dialog, dialog1)
                } else {
                    dialog.dismiss()
                }
            }

            override fun onCancelled(snapshot: DatabaseError) {
                logData("Normal", snapshot.message)
                dialog.dismiss()
            }
        })

        dbrefPromo.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val productList = handleSnapshotPromo(snapshot)
                    dataPromo = productList
                    logData("Promo", snapshot.value.toString())
                    checkDataLoaded(dialog, dialog1)
                } else {
                    dialog1.dismiss()
                }
            }

            override fun onCancelled(snapshot: DatabaseError) {
                logData("Promo", snapshot.message)
                dialog1.dismiss()
            }
        })
    }

    private fun checkDataLoaded(dialog: Dialog, dialog1: Dialog) {
        // Cek apakah dataNormal atau dataPromo sudah diinisialisasi atau tidak.
        // Jika ya, maka tutup kedua dialog.
        if (this::dataNormal.isInitialized || this::dataPromo.isInitialized) {
            dialog.dismiss()
            dialog1.dismiss()
            setupRecyclerView()
        }
        // Jika salah satu data tidak ada, maka cek apakah yang lain sudah diinisialisasi.
        // Jika sudah, maka tutup dialog yang terkait dengan data yang ada.
        else if (!this::dataNormal.isInitialized && this::dataPromo.isInitialized) {
            dialog1.dismiss()
            setupRecyclerView()
        } else if (this::dataNormal.isInitialized && !this::dataPromo.isInitialized) {
            dialog.dismiss()
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        RecyclerView = findViewById(R.id.RecyclerView)
        RecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RiwayatAdapter(dataNormal ?: emptyList(), dataPromo ?: emptyList(), this)
        RecyclerView.adapter = adapter
    }


    fun handleSnapshot(snapshot: DataSnapshot): List<PrintLogPricetagNormal>{
        val list = mutableListOf<PrintLogPricetagNormal>()
        for (snap in snapshot.children) {
            for (child in snap.children) {
                val log = PrintLogPricetagNormal(
                    snap.key.toString(),
                    child.key.toString(),
                    child.value as List<PricetagNormal>
                )
                list.add(log)
            }
        }
        return list
    }

    fun handleSnapshotPromo(snapshot: DataSnapshot): List<PrintLogLogPricetagPromo>{
        val list = mutableListOf<PrintLogLogPricetagPromo>()
        for (snap in snapshot.children) {
            for (child in snap.children) {
                val log = PrintLogLogPricetagPromo(
                    snap.key.toString(),
                    child.key.toString(),
                    child.value as List<PricetagPromo>
                )
                list.add(log)
            }
        }
        return list
    }


    private fun logData(tag: String, message: String) {
        Log.d(tag, "$tag : $message")
    }

    override fun onItemClick(item: PrintLogItem) {
        when (item) {
            is PrintLogItem.Normal -> {
                val file = File(filesDir, "${item.data.Nama}.pdf")
                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)

            }
            is PrintLogItem.Promo -> {
                // Handle click for promo item
                val file = File(filesDir, "${item.data.Nama}.pdf")
                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
            }
        }
    }
}