package com.anggaa.projectpricetag.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.PropertyName

@Entity(tableName = "Produk")
data class Produk(
    @set:PropertyName("code")
    @get:PropertyName("code")
    var code: String,

    @set:PropertyName("qty")
    @get:PropertyName("qty")
    var qty: Int,

    @set:PropertyName("supplier")
    @get:PropertyName("supplier")
    var supplier: String,

    @set:PropertyName("name")
    @get:PropertyName("name")
    var name: String,

    @PrimaryKey
    @set:PropertyName("id")
    @get:PropertyName("id")
    var id: String,

    @set:PropertyName("barcode")
    @get:PropertyName("barcode")
    var barcode: String
){
    constructor() : this("", 0, "", "", "", "")
}
