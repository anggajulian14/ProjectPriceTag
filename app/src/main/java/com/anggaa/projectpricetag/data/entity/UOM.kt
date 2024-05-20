package com.anggaa.projectpricetag.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UOM (
    @PrimaryKey
    val id: Int,
    val uom: String
)