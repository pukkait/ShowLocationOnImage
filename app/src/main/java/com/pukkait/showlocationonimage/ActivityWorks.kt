package com.pukkait.showlocationonimage

import android.content.Context
import android.widget.Toast

object ActivityWorks {
    fun didIt(activity: Context) {
        Toast.makeText(activity, "Hello World!", Toast.LENGTH_SHORT).show()
    }
}