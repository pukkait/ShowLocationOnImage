package com.pukkait.showlocationonimage

import android.content.Context
import android.widget.Toast

object TestToast {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}