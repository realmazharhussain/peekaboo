package com.preat.peekaboo.ui.camera

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFRetain
import platform.CoreMedia.CMSampleBufferRef

@OptIn(ExperimentalForeignApi::class)
data class IosCameraFrame(val buffer: CMSampleBufferRef) : CameraFrame {
    init { CFRetain(buffer) }
    override fun close() = CFRelease(buffer)
}

@OptIn(ExperimentalForeignApi::class)
fun CMSampleBufferRef.asCameraFrame() = IosCameraFrame(buffer = this)

@OptIn(ExperimentalForeignApi::class)
fun CameraFrame.buffer() = (this as IosCameraFrame).buffer
