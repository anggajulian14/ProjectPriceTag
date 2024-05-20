package com.anggaa.projectpricetag.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anggaa.projectpricetag.data.entity.Pricetagnormal

@Dao
interface PriceTagNormalDAO {

    @Query("SELECT * FROM Pricetagnormal")
    fun getAll(): List<Pricetagnormal>

    @Query("SELECT * FROM Pricetagnormal WHERE barcode = :barcode")
    fun getByBarcode(barcode: String): Pricetagnormal

    @Query("DELETE FROM Pricetagnormal")
    fun deleteAll()

    @Query("DELETE FROM Pricetagnormal WHERE barcode = :barcode")
    fun deleteByBarcode(barcode: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pricetagnormal: Pricetagnormal): Long

    @Query("UPDATE Pricetagnormal SET barcode = :barcode WHERE barcode = :oldBarcode")
    fun updateBarcode(barcode: String, oldBarcode: String)

    @Query("UPDATE Pricetagnormal SET Nama = :nama, Kategori = :kategori, Brand = :brand, Supplier = :supplier WHERE barcode = :barcode")
    fun updateAll(barcode: String, nama : String, kategori :String, supplier: String, brand: String)

    @Query("SELECT * FROM Pricetagnormal WHERE Nama = :name")
    fun getName(name: String): Pricetagnormal

    @Query("SELECT * FROM Pricetagnormal WHERE barcode = :barcode")
    fun getBarcode(barcode: String): Pricetagnormal

    @Query("SELECT * FROM Pricetagnormal WHERE Kategori = :Kategori")
    fun getKategori(Kategori: String): List<Pricetagnormal>

    @Query("SELECT * FROM Pricetagnormal WHERE Brand = :Brand")
    fun getBrand(Brand: String): List<Pricetagnormal>

    @Query("SELECT * FROM Pricetagnormal WHERE Supplier = :Supplier")
    fun getSupplier(Supplier: String): List<Pricetagnormal>

    // Mengembalikan true jika tabel kosong, false jika tidak
    @Query("SELECT COUNT(*) == 0 FROM Pricetagnormal")
    fun isEmpty(): Boolean

}