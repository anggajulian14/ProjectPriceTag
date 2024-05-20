package com.anggaa.projectpricetag.model

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class DecimalTextWatcher(private val editText: EditText) : TextWatcher {
    private val decimalFormat: DecimalFormat

    init {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.groupingSeparator = '.'
        decimalFormat = DecimalFormat("#,###", symbols)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        editText.removeTextChangedListener(this)

        try {
            val originalString = s.toString()

            // Remove the existing commas before formatting
            val cleanString = originalString.replace("[^\\d]".toRegex(), "")

            if (cleanString.isNotEmpty()) {
                val longVal = cleanString.toLong()
                val formattedString = decimalFormat.format(longVal)
                editText.setText(formattedString)
                editText.setSelection(editText.text!!.length)
            } else {
                editText.setText("")
            }

        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        }

        editText.addTextChangedListener(this)
    }
}