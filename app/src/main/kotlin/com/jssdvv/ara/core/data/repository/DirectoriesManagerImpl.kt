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
        private const val MACHINE_DIR = "machine"
        private const val MEDIA_DIR = "media"
        private const val MARKER_DIR = "marker"
        private const val DOC_DIR = "document"
        private const val MODEL_DIR = "model"
        private const val VECTOR_DIR = "vector"
        private const val TOOLS_DIR = "tools"
    }

    private val internalStorageDir by lazy { context.filesDir }

    private fun checkDir(file: File) { if (!file.exists()) file.mkdirs() }

    override fun getRootMachineDir() = File(internalStorageDir, MACHINE_DIR)
        .also(::checkDir)

    override fun getMachineDir(machineId: Int) = File(getRootMachineDir(), machineId.toString())
        .also(::checkDir)

    override fun getMarkersDir(machineId: Int) = File(getMachineDir(machineId), MARKER_DIR)
        .also(::checkDir)

    override fun getMediaFilesDir(machineId: Int) = File(getMachineDir(machineId), MEDIA_DIR)
        .also(::checkDir)

    override fun getDocumentsDir(machineId: Int) = File(getMachineDir(machineId), DOC_DIR)
        .also(::checkDir)

    override fun getModelsDir(machineId: Int) = File(getMachineDir(machineId), MODEL_DIR)
        .also(::checkDir)

    override fun getVectorDir(machineId: Int) = File(getRootMachineDir(), VECTOR_DIR)
        .also(::checkDir)

    override fun getToolsMediaDir() = File(internalStorageDir, TOOLS_DIR)
        .also(::checkDir)
}