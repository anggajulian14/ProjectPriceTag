package com.anggaa.projectpricetag.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.view.activity.DetailProduk
import com.anggaa.projectpricetag.view.activity.RiwayatActivity
import java.util.Calendar

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting: String
        greeting = if (hourOfDay < 12) {
            "Selamat Pagi"
        } else if (hourOfDay < 18) {
            "Selamat Siang"
        } else if (hourOfDay < 22) {
            "Selamat Malam"
        } else {
            "Selamat Malam"
        }
        view.findViewById<TextView>(R.id.Sambutan).text = greeting

        view.findViewById<LinearLayout>(R.id.ubahProfile).setOnClickListener {
            val intent = Intent(requireContext(), DetailProduk::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.Riwayat).setOnClickListener {
            val intent = Intent(requireContext(), RiwayatActivity::class.java)
            startActivity(intent)
        }
    }

}