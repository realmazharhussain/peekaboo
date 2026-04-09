package com.preat.peekaboo.ui.camera

import androidx.camera.core.ImageProxy

data class AndroidCameraFrame(val imageProxy: ImageProxy, override val tag: Int = 0) : CameraFrame {
    override fun close() {
        imageProxy.close()
        super.close()
    }
}

fun ImageProxy.asCameraFrame(tag: Int = 0) = AndroidCameraFrame(imageProxy = this, tag = tag)
fun CameraFrame.imageProxy() = (this as AndroidCameraFrame).imageProxy
