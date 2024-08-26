package com.pukkait.showlocationonimage.helper

interface ImageResultListener {
    fun onImageProcessed(imagePath: String, actionCode: Int)
    fun onError(errorMessage: String, actionCode: Int)
}
