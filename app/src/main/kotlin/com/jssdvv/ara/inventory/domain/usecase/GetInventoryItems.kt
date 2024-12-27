package com.jssdvv.ara.inventory.domain.usecase

import com.jssdvv.ara.core.domain.utility.OrderType
import com.jssdvv.ara.inventory.domain.models.InventoryItemEntity
import com.jssdvv.ara.inventory.domain.repository.InventoryItemRepository
import com.jssdvv.ara.inventory.domain.utility.InventoryItemOrderKey
import kotlinx.coroutines.flow.Flow

class GetInventoryItems(
    private val repository: InventoryItemRepository
) {
    operator fun invoke(
        orderKey: InventoryItemOrderKey = InventoryItemOrderKey.Id(OrderType.ASCENDING),
    ): Flow<List<InventoryItemEntity>> {
        return when (orderKey.orderType) {
            OrderType.ASCENDING -> when (orderKey) {
                is InventoryItemOrderKey.Id -> repository.getAllInventoryItemsByIdAsc()
                is InventoryItemOrderKey.Name -> repository.getAllInventoryItemsByNameAsc()
                is InventoryItemOrderKey.Timestamp -> repository.getAllInventoryItemsByTimestampAsc()
            }

            OrderType.DESCENDING -> when (orderKey) {
                is InventoryItemOrderKey.Id -> repository.getAllInventoryItemsByIdDesc()
                is InventoryItemOrderKey.Name -> repository.getAllInventoryItemsByNameDesc()
                is InventoryItemOrderKey.Timestamp -> repository.getAllInventoryItemsByTimestampDesc()
            }
        }
    }
}