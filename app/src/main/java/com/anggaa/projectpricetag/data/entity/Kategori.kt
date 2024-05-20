package com.anggaa.projectpricetag.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Kategori")
data class Kategori(
    @PrimaryKey
    val id: Int,

    val kategori: String
)