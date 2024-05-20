package com.anggaa.projectpricetag.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.view.activity.PriceTagNormal
import com.anggaa.projectpricetag.view.activity.PriceTagPromo

class HomeFragment : Fragment() {

    private lateinit var PriceTagPromoButton: Button
    private lateinit var PriceTagNormalButtton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PriceTagNormalButtton = view.findViewById<Button>(R.id.PricetagNormal)
        PriceTagNormalButtton.setOnClickListener {
            startActivity(
                Intent(requireContext(), PriceTagNormal::class.java)
            )
        }

        PriceTagPromoButton = view.findViewById(R.id.PricetagPromo)
        PriceTagPromoButton.setOnClickListener {
            startActivity(
                Intent(requireContext(), PriceTagPromo::class.java)
            )
        }
    }

}