package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.machines.domain.model.Document
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun countModelsByMachineId(machineId: Int): Flow<Int>
    fun selectModelByMachineIdAndName( machineId: Int, name: String): Document?
    fun selectDocumentsByMachineId(machineId: Int): Flow<List<Document>>
    suspend fun upsertDocument(vararg model: Document)
    suspend fun deleteDocument(vararg model: Document)
}