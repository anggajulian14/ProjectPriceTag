package com.anggaa.projectpricetag.model

import com.google.firebase.database.PropertyName

data class data(
    @set:PropertyName("code")
    @get:PropertyName("code")
    var code: Any,

    @set:PropertyName("qty")
    @get:PropertyName("qty")
    var qty: Int,

    @set:PropertyName("supplier")
    @get:PropertyName("supplier")
    var supplier: String,

    @set:PropertyName("name")
    @get:PropertyName("name")
    var name: String,

    @set:PropertyName("id")
    @get:PropertyName("id")
    var id: Int,

    @set:PropertyName("barcode")
    @get:PropertyName("barcode")
    var barcode: Any
){
    constructor() : this("", 0, "", "", 0, "")
}

