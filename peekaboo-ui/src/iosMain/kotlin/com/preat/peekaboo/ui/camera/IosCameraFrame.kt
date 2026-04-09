package com.preat.peekaboo.ui.camera

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFRetain
import platform.CoreMedia.CMSampleBufferRef

@OptIn(ExperimentalForeignApi::class)
data class IosCameraFrame(val buffer: CMSampleBufferRef, override val tag: Int = 0) : CameraFrame {
    init { CFRetain(buffer) }
    override fun close() {
        CFRelease(buffer)
        super.close()
    }
}

@OptIn(ExperimentalForeignApi::class)
fun CMSampleBufferRef.asCameraFrame(tag: Int = 0) = IosCameraFrame(buffer = this, tag = tag)

@OptIn(ExperimentalForeignApi::class)
fun CameraFrame.buffer() = (this as IosCameraFrame).buffer
