package com.anggaa.projectpricetag.view.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.data.AppDatabase
import com.anggaa.projectpricetag.data.entity.Pricetagnormal
import com.anggaa.projectpricetag.data.entity.Produk
import com.anggaa.projectpricetag.model.data
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class SplashScreen : AppCompatActivity() {

    private lateinit var ListData: MutableList<data>
    private lateinit var database: AppDatabase

    private lateinit var dbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi referensi database
       dbref = FirebaseDatabase.getInstance().reference

        // Membaca nilai boolean dari Firebase
        readBooleanValue("rate") { rate ->
            if (rate != null) {
                logData("Rate", rate.toString())
            } else {
                logData("Rate", "Tidak Ada")
            }
        }

        database = AppDatabase.getInstance(this)

        val isEmpty = database.dataDAO().isEmpty()
        if (!isEmpty) {
            val kosong = database.PricetagnormalDAO().isEmpty()
            if (kosong) {
                database.PricetagnormalDAO().deleteAll()
                for (data in database.dataDAO().getAll()) {
                    logData("Item", data.name)
                    database.PricetagnormalDAO().insert(
                        Pricetagnormal(
                            Nama = data.name,
                            Barcode = data.barcode,
                            Kategori = "",
                            Supplier = data.supplier,
                            Brand = "",
                            UOM = "",
                            Harga = "",
                            TGC = "",
                            Returan = "",
                            AlamatRak = ""
                        )
                    )
                }
            }else{
                logData("PriceTagNormal", "Tidak Kosong")
            }
            readBooleanValue("rate") { rate ->
                if (rate != null) {
                    if (rate) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Rate Us 5 Star", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    logData("Rate", "Tidak Ada")
                }
            }

        } else {
            getDataWithDelay()
        }
    }

    private fun readBooleanValue(path: String, callback: (Boolean?) -> Unit) {
        dbref.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Boolean::class.java)
                callback(value)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getDataWithDelay() {
        val delayInMillis: Long = 5000 // 5 detik

        // Membuat handler
        val handler = Handler(Looper.getMainLooper())

        // Menjalankan getData() setelah penundaan
        handler.postDelayed({
            getData()
        }, delayInMillis)
    }


    private fun getData() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        database = AppDatabase.getInstance(this)

        database.dataDAO().deleteAll()

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

                    val ID = UUID.randomUUID().toString()
                    database.dataDAO().insert(
                        Produk(
                            code = data.code.toString(),
                            qty = data.qty,
                            supplier = data.supplier,
                            name = data.name,
                            id = ID,
                            barcode = data.barcode.toString()
                        )
                    )
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
                        Toast.makeText(this@SplashScreen, "Data Loaded", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SplashScreen, HomeScreen::class.java)
                        startActivity(intent)
                        finish()
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

    private fun logData(tag:String, data: String) {
        Log.d(tag, "$tag, $data")
    }
}