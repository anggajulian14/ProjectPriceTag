package com.anggaa.projectpricetag.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.anggaa.projectpricetag.data.entity.Supplier

@Dao
interface SupplierDAO {

    @Query("SELECT * FROM Supplier")
    fun getAll(): List<Supplier>

    @Query("SELECT * FROM Supplier WHERE id = :id")
    fun getById(id: Int): Supplier

    @Query("SELECT * FROM Supplier WHERE supplier = :supplier")
    fun getBySupplier(supplier: String): Supplier

    @Query("DELETE FROM Supplier")
    fun deleteAll()

    @Query("DELETE FROM Supplier WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM Supplier WHERE supplier = :supplier")
    fun deleteBySupplier(supplier: String)

    @Query("INSERT INTO Supplier (id, supplier) VALUES (:id, :supplier)")
    fun insert(id: Int, supplier: String)

    @Query("UPDATE Supplier SET supplier = :supplier WHERE id = :id")
    fun update(id: Int, supplier: String)



}


