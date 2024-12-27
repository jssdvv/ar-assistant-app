package com.jssdvv.ara.inventory.domain.usecase

data class InventoryItemUseCases(
    val getInventoryItems: GetInventoryItems,
    val insertInventoryItem: InsertInventoryItem,
    val updateInventoryItem: UpdateInventoryItem,
    val deleteInventoryItem: DeleteInventoryItem
)