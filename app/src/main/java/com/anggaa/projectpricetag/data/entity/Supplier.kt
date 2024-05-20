package com.anggaa.projectpricetag.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Supplier (
    @PrimaryKey
    val id: Int,
    val supplier: String
)