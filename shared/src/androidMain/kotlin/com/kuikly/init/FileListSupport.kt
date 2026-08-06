package com.kuikly.init

import java.io.File

internal actual fun listFilesInDirectory(dir: String): List<String> {
    return try {
        val directory = File(dir)
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }
}
