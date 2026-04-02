/*
 * Copyright 2023-2024 onseok
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

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreImage.CIImage
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectMake
import platform.CoreVideo.CVPixelBufferRef
import platform.Foundation.NSData
import platform.UIKit.UIImage
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.posix.memcpy

private const val PREMULTIPLIED_RGBA_BITMAP_INFO: UInt = 1u

@OptIn(ExperimentalForeignApi::class)
internal fun CVPixelBufferRef.toImageBitmapOrNull(): ImageBitmap? {
    val ciImage = CIImage.imageWithCVPixelBuffer(this)
    val uiImage = UIImage.imageWithCIImage(ciImage)
    val size = uiImage.size.useContents { Pair(width, height) }

    UIGraphicsBeginImageContextWithOptions(uiImage.size, false, 1.0)
    uiImage.drawInRect(
        CGRectMake(
            x = 0.0,
            y = 0.0,
            width = size.first,
            height = size.second,
        ),
    )
    val renderedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    val cgImage = renderedImage?.CGImage ?: return null
    val width = CGImageGetWidth(cgImage).toInt()
    val height = CGImageGetHeight(cgImage).toInt()
    val bytesPerRow = width * 4
    val bytes = ByteArray(height * bytesPerRow)

    return bytes.usePinned { pinned ->
        val colorSpace = CGColorSpaceCreateDeviceRGB() ?: return@usePinned null
        val context =
            CGBitmapContextCreate(
                data = pinned.addressOf(0),
                width = width.toULong(),
                height = height.toULong(),
                bitsPerComponent = 8u,
                bytesPerRow = bytesPerRow.toULong(),
                space = colorSpace,
                bitmapInfo = PREMULTIPLIED_RGBA_BITMAP_INFO,
            )
        if (context == null) {
            CGColorSpaceRelease(colorSpace)
            return@usePinned null
        }

        try {
            CGContextDrawImage(
                context,
                CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()),
                cgImage,
            )
            val imageInfo = ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL)
            Image.makeRaster(imageInfo, bytes, bytesPerRow).toComposeImageBitmap()
        } finally {
            CGContextRelease(context)
            CGColorSpaceRelease(colorSpace)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toPeekabooByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return byteArray
}
