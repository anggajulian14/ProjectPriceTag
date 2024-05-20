package com.anggaa.projectpricetag.Produk.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.anggaa.projectpricetag.data.entity.Produk

@Dao
interface DataDAO {

    @Query("SELECT * FROM Produk")
    fun getAll(): List<Produk>

    @Query("SELECT * FROM Produk WHERE id = :id")
    fun getById(id: Int): Produk

    @Query("SELECT * FROM Produk WHERE code = :code")
    fun getByCode(code: String): Produk

    @Query("SELECT * FROM Produk WHERE name = :name")
    fun getByName(name: String): Produk

    @Query("SELECT * FROM Produk WHERE supplier = :supplier")
    fun getBySupplier(supplier: String): Produk

    @Query("SELECT * FROM Produk WHERE qty = :qty")
    fun getByQty(qty: Int): Produk

    @Query("SELECT * FROM Produk WHERE barcode = :barcode")
    fun getByBarcode(barcode: String): Produk

    @Query("DELETE FROM Produk")
    fun deleteAll()

    @Insert
    fun insert(Produk: Produk)

    @Insert
    fun insertAll(vararg Produk: Produk)

    @Query("DELETE FROM Produk WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM Produk WHERE code = :code")
    fun deleteByCode(code: String)

    @Query("DELETE FROM Produk WHERE name = :name")
    fun deleteByName(name: String)

    @Query("DELETE FROM Produk WHERE supplier = :supplier")
    fun deleteBySupplier(supplier: String)

    @Query("DELETE FROM Produk WHERE qty = :qty")
    fun deleteByQty(qty: Int)

    @Query("DELETE FROM Produk WHERE barcode = :barcode")
    fun deleteByBarcode(barcode: String)

    @Query("UPDATE Produk SET code = :code, qty = :qty, supplier = :supplier, name = :name, barcode = :barcode WHERE id = :id")
    fun update(id: Int, code: String, qty: Int, supplier: String, name: String, barcode: String)

    // Mengembalikan jumlah entri dalam tabel
    @Query("SELECT COUNT(*) FROM produk")
    fun getCount(): Int

    // Mengembalikan true jika tabel kosong, false jika tidak
    @Query("SELECT COUNT(*) == 0 FROM produk")
    fun isEmpty(): Boolean
}