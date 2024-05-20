    package com.anggaa.projectpricetag.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anggaa.projectpricetag.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

    class HomeScreen : AppCompatActivity() {

    private lateinit var PriceTagNormalButtton : Button
    private lateinit var PriceTagPromoButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        PriceTagNormalButtton = findViewById(R.id.PricetagNormal)
        PriceTagNormalButtton.setOnClickListener {
            startActivity(
                Intent(this, PriceTagNormal::class.java)
            )
        }

        PriceTagPromoButton = findViewById(R.id.PricetagPromo)
        PriceTagPromoButton.setOnClickListener {
            startActivity(
                Intent(this, PriceTagPromo::class.java)
            )
        }

    }
}