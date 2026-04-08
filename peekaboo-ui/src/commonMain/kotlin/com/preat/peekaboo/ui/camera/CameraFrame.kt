package com.preat.peekaboo.ui.camera

interface CameraFrame : AutoCloseable {
    fun tag(): Int = 0
    override fun close() {}
}
