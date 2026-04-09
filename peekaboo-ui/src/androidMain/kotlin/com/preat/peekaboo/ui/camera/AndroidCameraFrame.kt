/*
 * Copyright 2026 onseok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
