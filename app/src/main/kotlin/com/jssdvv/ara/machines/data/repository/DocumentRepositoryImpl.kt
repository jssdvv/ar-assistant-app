package com.jssdvv.ara.machines.data.repository

import com.jssdvv.ara.machines.data.local.dao.DocumentDao
import com.jssdvv.ara.machines.data.local.entity.DocumentEntity
import com.jssdvv.ara.machines.data.local.mapper.toDomain
import com.jssdvv.ara.machines.data.local.mapper.toEntity
import com.jssdvv.ara.machines.domain.model.Document
import com.jssdvv.ara.machines.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepositoryImpl(
    private val dao: DocumentDao,
) : DocumentRepository {

    override fun countModelsByMachineId(machineId: Int): Flow<Int> =
        dao.countDocumentsByMachineId(machineId)

    override fun selectModelByMachineIdAndName(machineId: Int, name: String): Document? =
        dao.selectDocumentByMachineIdAndName(machineId, name)?.toDomain()

    override fun selectDocumentsByMachineId(machineId: Int): Flow<List<Document>> =
        dao.selectDocumentsByMachineId(machineId).map { it.map(DocumentEntity::toDomain) }

    override suspend fun upsertDocument(vararg model: Document) =
        dao.upsertDocument(*model.map(Document::toEntity).toTypedArray())

    override suspend fun deleteDocument(vararg model: Document) =
        dao.deleteDocument(*model.map(Document::toEntity).toTypedArray())
}