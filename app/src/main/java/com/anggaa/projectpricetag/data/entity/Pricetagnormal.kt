package com.anggaa.projectpricetag.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pricetagnormal (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val Nama: String,
    val Barcode: String,
    val Kategori: String,
    val Supplier: String,
    val Brand: String,
    val UOM: String,
    val Harga: String,
    val TGC: String,
    val Returan: String,
    val AlamatRak: String
)