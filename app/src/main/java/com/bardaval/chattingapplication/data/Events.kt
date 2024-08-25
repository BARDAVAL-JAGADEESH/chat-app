package com.bardaval.chattingapplication.data

import android.content.Context

open class Events<out T>(val context: T) {
                                           //this class basically handle all type of exception
    var hasBeenHandled=false

    fun getContentOrNull():T? {
        return  if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            context
        }

    }
}