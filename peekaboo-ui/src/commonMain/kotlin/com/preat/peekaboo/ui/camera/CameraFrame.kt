package com.preat.peekaboo.ui.camera

import androidx.annotation.CallSuper

interface CameraFrame : AutoCloseable {
    val tag: Int
    @CallSuper
    override fun close() = println("closing $tag")
}
