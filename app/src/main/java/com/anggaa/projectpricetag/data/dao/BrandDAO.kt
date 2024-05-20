package com.anggaa.projectpricetag.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.anggaa.projectpricetag.data.entity.Brand

@Dao
interface BrandDAO {

    @Query("SELECT * FROM Brand")
    fun getAll(): List<Brand>

    @Query("SELECT * FROM Brand WHERE id = :id")
    fun getById(id: Int): Brand

    @Query("SELECT * FROM Brand WHERE brand = :brand")
    fun getByBrand(brand: String): Brand

    @Query("DELETE FROM Brand")
    fun deleteAll()

    @Query("DELETE FROM Brand WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM Brand WHERE brand = :brand")
    fun deleteByBrand(brand: String)

    @Query("INSERT INTO Brand (id, brand) VALUES (:id, :brand)")
    fun insert(id: Int, brand: String)

    @Query("UPDATE Brand SET brand = :brand WHERE id = :id")
    fun update(id: Int, brand: String)
}