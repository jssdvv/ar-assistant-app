package com.jssdvv.ara.core.data.repository

import android.content.Context
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import java.io.File

/**
 * Base class for constructing the structure of the app's directories.
 */
class DirectoriesManagerImpl(
    private val context: Context,
) : DirectoriesManager {
    companion object {
        private const val MACHINE_DIRECTORY_NAME = "machine"
        private const val MEDIA_DIRECTORY_NAME = "media"
        private const val MARKER_DIRECTORY_NAME = "marker"
        private const val DOCUMENT_DIRECTORY_NAME = "document"
        private const val MODEL_DIRECTORY_NAME = "model"
        private const val VECTOR_DIRECTORY_NAME = "vector"
    }

    private val internalStorageDir by lazy { context.filesDir }

    override fun getMachineDir(machineId: Int): File =
        File(internalStorageDir, "$MACHINE_DIRECTORY_NAME/$machineId").apply {
            if (!exists()) mkdirs()
        }

    override fun getMarkersDir(machineId: Int): File =
        File(getMachineDir(machineId), MARKER_DIRECTORY_NAME).apply {
            if (!exists()) mkdirs()
        }

    override fun getMediaFilesDir(machineId: Int): File =
        File(getMachineDir(machineId), MEDIA_DIRECTORY_NAME).apply {
            if (!exists()) mkdirs()
        }

    override fun getDocumentsDir(machineId: Int): File =
        File(getMachineDir(machineId), DOCUMENT_DIRECTORY_NAME).apply {
            if (!exists()) mkdirs()
        }

    override fun getModelsDir(machineId: Int): File =
        File(getMachineDir(machineId), MODEL_DIRECTORY_NAME).apply {
            if (!exists()) mkdirs()
        }

    override fun getVectorDir(machineId: Int) : File =
        File(internalStorageDir, "$MACHINE_DIRECTORY_NAME/$VECTOR_DIRECTORY_NAME").apply {
            if (!exists()) mkdirs()
        }
}