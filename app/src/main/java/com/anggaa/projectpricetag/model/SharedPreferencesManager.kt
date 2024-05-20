package com.anggaa.projectpricetag.model

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

object SharedPreferencesManager {

    private const val PREF_NAME = "ProductPreferences"
    private const val KEY_PRODUCT_LIST_NORMAL = "productListNormal"
    private const val KEY_PRODUCT_LIST_PROMO = "productListPromo"

    // Simpan item produk normal ke SharedPreferences
    fun saveProductItem(context: Context, newItem: PricetagNormal) {
        val productListNormal = getProductListNormal(context).toMutableList()
        productListNormal.add(newItem)
        saveProductListNormal(context, productListNormal)
    }

    // Simpan item produk promo ke SharedPreferences
    fun saveProductItem(context: Context, newItem: PricetagPromo) {
        val productListPromo = getProductListPromo(context).toMutableList()
        productListPromo.add(newItem)
        saveProductListPromo(context, productListPromo)
    }

    // Ambil data list produk normal dari SharedPreferences
    fun getProductListNormal(context: Context): List<PricetagNormal> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_PRODUCT_LIST_NORMAL, null)
        val type = object : TypeToken<List<PricetagNormal>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // Ambil data list produk promo dari SharedPreferences
    fun getProductListPromo(context: Context): List<PricetagPromo> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_PRODUCT_LIST_PROMO, null)
        val type = object : TypeToken<List<PricetagPromo>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // Hapus item produk normal dari SharedPreferences
    fun removeProductItem(context: Context, removedItem: PricetagNormal) {
        val productListNormal = getProductListNormal(context).toMutableList()
        productListNormal.remove(removedItem)
        saveProductListNormal(context, productListNormal)
    }

    // Hapus item produk promo dari SharedPreferences
    fun removeProductItem(context: Context, removedItem: PricetagPromo) {
        val productListPromo = getProductListPromo(context).toMutableList()
        productListPromo.remove(removedItem)
        saveProductListPromo(context, productListPromo)
    }

    // Simpan data list produk normal ke SharedPreferences
    private fun saveProductListNormal(context: Context, productList: List<PricetagNormal>) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(productList)
        editor.putString(KEY_PRODUCT_LIST_NORMAL, json)
        editor.apply()
    }

    // Simpan data list produk promo ke SharedPreferences
    private fun saveProductListPromo(context: Context, productList: List<PricetagPromo>) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(productList)
        editor.putString(KEY_PRODUCT_LIST_PROMO, json)
        editor.apply()
    }

    // Hapus data list produk dari SharedPreferences
    fun clearProductListNormal(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(KEY_PRODUCT_LIST_NORMAL)
        editor.apply()
    }

    fun clearProductListPromo(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(KEY_PRODUCT_LIST_PROMO)
        editor.apply()
    }
}