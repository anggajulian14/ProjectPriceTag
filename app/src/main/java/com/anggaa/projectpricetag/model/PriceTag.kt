package com.anggaa.projectpricetag.model

data class PricetagNormal(
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
){
    constructor(): this("", "", "", "", "", "", "", "", "", "")
}

data class PricetagPromo(
    val Nama: String,
    val Barcode: String,
    val Kategori: String,
    val Supplier: String,
    val Brand: String,
    val TanggalMulai: String,
    val TanggalAkhir: String,
    val HargaNormal: String,
    val HargaPromo: String,
    val TGC: String,
    val Returan: String,
    val AlamatRak: String
)