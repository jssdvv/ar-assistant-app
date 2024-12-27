package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.core.domain.utility.OrderType
import com.jssdvv.ara.machinery.domain.model.MachineEntity
import com.jssdvv.ara.machinery.domain.repository.MachineRepository
import com.jssdvv.ara.machinery.domain.utility.MachineOrderKey
import kotlinx.coroutines.flow.Flow

class GetMachines(
    private val repository: MachineRepository
) {
    operator fun invoke(
        orderKey: MachineOrderKey = MachineOrderKey.Name(OrderType.ASCENDING),
    ): Flow<List<MachineEntity>> {
        return when (orderKey.orderType) {
            OrderType.ASCENDING -> when (orderKey) {
                is MachineOrderKey.Name -> repository.getAllMachinesByNameAsc()
                is MachineOrderKey.Category -> repository.getAllMachinesByCategoryAsc()
                is MachineOrderKey.Timestamp -> repository.getAllMachinesByTimestampAsc()
            }

            OrderType.DESCENDING -> when (orderKey) {
                is MachineOrderKey.Name -> repository.getAllMachinesByNameDesc()
                is MachineOrderKey.Category -> repository.getAllMachinesByCategoryDesc()
                is MachineOrderKey.Timestamp -> repository.getAllMachinesByTimestampDesc()
            }
        }
    }
}