package com.anggaa.projectpricetag.data

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anggaa.projectpricetag.Produk.dao.DataDAO
import com.anggaa.projectpricetag.data.dao.BrandDAO
import com.anggaa.projectpricetag.data.dao.KategoriDAO
import com.anggaa.projectpricetag.data.dao.PriceTagNormalDAO
import com.anggaa.projectpricetag.data.dao.SupplierDAO
import com.anggaa.projectpricetag.data.dao.UOMDAO
import com.anggaa.projectpricetag.data.entity.Brand
import com.anggaa.projectpricetag.data.entity.Kategori
import com.anggaa.projectpricetag.data.entity.Pricetagnormal
import com.anggaa.projectpricetag.data.entity.Produk
import com.anggaa.projectpricetag.data.entity.Supplier
import com.anggaa.projectpricetag.data.entity.UOM

@Database(entities = [
    Produk::class,
    Kategori::class,
    Supplier::class,
    Brand::class,
    UOM::class,
    Pricetagnormal::class], version = 13, exportSchema = false)

abstract  class AppDatabase : RoomDatabase() {

    abstract fun dataDAO(): DataDAO
    abstract fun KategoriDAO(): KategoriDAO
    abstract fun SupplierDAO(): SupplierDAO
    abstract fun UOMDAO(): UOMDAO
    abstract fun BrandDAO(): BrandDAO
    abstract fun PricetagnormalDAO(): PriceTagNormalDAO

    companion object {
        private var instance : AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}