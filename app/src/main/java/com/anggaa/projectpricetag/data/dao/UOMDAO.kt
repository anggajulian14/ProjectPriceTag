package com.anggaa.projectpricetag.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.anggaa.projectpricetag.data.entity.UOM

@Dao
interface UOMDAO {

    @Query("SELECT * FROM UOM")
    fun getAll(): List<UOM>

    @Query("SELECT * FROM UOM WHERE id = :id")
    fun getById(id: Int): UOM

    @Query("SELECT * FROM UOM WHERE uom = :uom")
    fun getByUom(uom: String): UOM

    @Query("DELETE FROM UOM")
    fun deleteAll()

    @Query("DELETE FROM UOM WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM UOM WHERE uom = :uom")
    fun deleteByUom(uom: String)

    @Query("INSERT INTO UOM (id, uom) VALUES (:id, :uom)")
    fun insert(id: Int, uom: String)

    @Query("UPDATE UOM SET uom = :uom WHERE id = :id")
    fun update(id: Int, uom: String)


}