package com.anggaa.projectpricetag.model

data class PrintLogPricetagNormal (
    var tanggal : String,
    var Nama: String,
    var data: List<PricetagNormal>
) {constructor(): this("", "", listOf())}

data class PrintLogLogPricetagPromo (
    var tanggal : String,
    var Nama: String,
    var data: List<PricetagPromo>
) {constructor(): this("", "", listOf())}
