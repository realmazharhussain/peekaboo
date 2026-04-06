package com.preat.peekaboo.ui.camera

import androidx.camera.core.ImageProxy

data class AndroidCameraFrame(val imageProxy: ImageProxy) : CameraFrame
fun ImageProxy.asCameraFrame() = AndroidCameraFrame(imageProxy = this)
fun CameraFrame.imageProxy() = (this as AndroidCameraFrame).imageProxy
