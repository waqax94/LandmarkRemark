package com.waqas.landmarkremark.presentation.common

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.lang.Exception

object Input {

    fun hideKeybord(activity: Activity) {
        var imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}