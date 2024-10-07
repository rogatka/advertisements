package com.advertisement.ads.utils

import org.apache.tika.Tika
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


object FileUtils {
    fun defineFileContentType(bytes: ByteArray): String {
        return Tika().detect(bytes)
    }

    fun scaleImage(input: ByteArray): ByteArray {
        val sourceImage: BufferedImage = ImageIO.read(ByteArrayInputStream(input))
        val width = sourceImage.width
        val height = sourceImage.height

        if (width > height) {
            val extraSize = (height - 100).toFloat()
            val percentHight = (extraSize / height) * 100
            val percentWidth = width - ((width / 100) * percentHight)
            val img = BufferedImage(percentWidth.toInt(), 100, BufferedImage.TYPE_INT_RGB)
            val scaledImage = sourceImage.getScaledInstance(percentWidth.toInt(), 100, Image.SCALE_SMOOTH)
            img.createGraphics().drawImage(scaledImage, 0, 0, null)
            val img2: BufferedImage = img.getSubimage(((percentWidth - 100) / 2).toInt(), 0, 100, 100)

            val byteArrayOutputStream = ByteArrayOutputStream()
            ImageIO.write(img2, "jpg", byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } else {
            val extraSize = (width - 100).toFloat()
            val percentWidth = (extraSize / width) * 100
            val percentHight = height - ((height / 100) * percentWidth)
            val img = BufferedImage(100, percentHight.toInt(), BufferedImage.TYPE_INT_RGB)
            val scaledImage = sourceImage.getScaledInstance(100, percentHight.toInt(), Image.SCALE_SMOOTH)
            img.createGraphics().drawImage(scaledImage, 0, 0, null)
            val img2: BufferedImage = img.getSubimage(0, ((percentHight - 100) / 2).toInt(), 100, 100)

            val byteArrayOutputStream = ByteArrayOutputStream()
            ImageIO.write(img2, "jpg", byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
    }
}