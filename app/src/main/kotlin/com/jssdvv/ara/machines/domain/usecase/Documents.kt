package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.machines.domain.model.Document
import com.jssdvv.ara.machines.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

data class DocumentsDataManager(
    val select: SelectDocuments,
    val upsert: UpsertDocuments,
    val delete: DeleteDocuments,
)

class CountDocuments(private val repository: DocumentRepository) {
    operator fun invoke(machineId: Int): Flow<Int> = repository.countModelsByMachineId(machineId)
}

class SelectDocuments(private val repository: DocumentRepository) {
    operator fun invoke(machineId: Int): Flow<List<Document>> =
        repository.selectDocumentsByMachineId(machineId)

    fun selectDocumentByMachineIdAndName(machineId: Int, name: String): Document? =
        repository.selectModelByMachineIdAndName(machineId, name)
}

class UpsertDocuments(private val repository: DocumentRepository) {
    suspend operator fun invoke(vararg model: Document) = repository.upsertDocument(*model)
}

class DeleteDocuments(private val repository: DocumentRepository) {
    suspend operator fun invoke(vararg model: Document) = repository.deleteDocument(*model)
}