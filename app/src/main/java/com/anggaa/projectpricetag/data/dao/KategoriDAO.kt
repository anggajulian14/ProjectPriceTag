package com.anggaa.projectpricetag.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.anggaa.projectpricetag.data.entity.Kategori

@Dao
interface KategoriDAO {

    @Query("SELECT * FROM Kategori")
    fun getAll(): List<Kategori>

    @Query("SELECT * FROM Kategori WHERE id = :id")
    fun getById(id: Int): Kategori

    @Query("SELECT * FROM Kategori WHERE kategori = :kategori")
    fun getByKategori(kategori: String): Kategori

    @Query("DELETE FROM Kategori")
    fun deleteAll()

    @Query("DELETE FROM Kategori WHERE id = :id")
    fun deleteById(id: Int)

    @Insert
    fun insertAll(kategori: Kategori)

    @Query("DELETE FROM Kategori WHERE kategori = :kategori")
    fun deleteByKategori(kategori: String)

    @Query("INSERT INTO Kategori (id, kategori) VALUES (:id, :kategori)")
    fun insert(id: Int, kategori: String)

    @Query("UPDATE Kategori SET kategori = :kategori WHERE id = :id")
    fun update(id: Int, kategori: String)
}
