package com.app.love_counter.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageStorageManager {

    /**
     * Sao chép tệp ảnh từ Uri (Thư viện/Photo Picker) vào Internal Storage riêng của ứng dụng
     * Vị trí lưu: /data/user/0/com.app.love_counter/files/avatars/
     * @return Uri cục bộ cố định vĩnh viễn (file:///data/...)
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri, fileName: String): Uri? {
        return try {
            val folder = File(context.filesDir, "avatars")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val destFile = File(folder, fileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(destFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Uri.fromFile(destFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}