package com.jssdvv.ara.core.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jssdvv.ara.machines.data.local.entity.ToolEntity
import com.jssdvv.ara.machines.domain.type.ToolType

class SeedDatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedTools(db)
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        val cursor = db.query("SELECT COUNT(*) FROM ${ToolEntity.TABLE_NAME}")
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        if (count == 0) {
            seedTools(db)
        }
    }

    private fun seedTools(db: SupportSQLiteDatabase) {
        data class SeedTool(
            val type: ToolType,
            val name: String,
            val code: String?,
        )

        val tools = listOf(
            SeedTool(ToolType.WRENCH, "Llave ajustable", "LA-001"),
            SeedTool(ToolType.WRENCH, "Llave Allen 4mm", "AL-004"),
            SeedTool(ToolType.WRENCH, "Llave de torque", "LT-010"),
            SeedTool(ToolType.WRENCH, "Llave española 13mm", "LE-013"),
            SeedTool(ToolType.WRENCH, "Llave de tubo", "LT-001"),
            SeedTool(ToolType.SCREWDRIVER, "Destornillador Phillips #2", "DP-002"),
            SeedTool(ToolType.SCREWDRIVER, "Destornillador plano 3mm", "DP-003"),
            SeedTool(ToolType.SCREWDRIVER, "Destornillador Torx T20", "DT-020"),
            SeedTool(ToolType.SCREWDRIVER, "Destornillador de precisión", "DP-000"),
            SeedTool(ToolType.PLIERS, "Alicate de corte", "AC-001"),
            SeedTool(ToolType.PLIERS, "Pinza de presión", "PP-001"),
            SeedTool(ToolType.PLIERS, "Alicate de punta", "AP-001"),
            SeedTool(ToolType.PLIERS, "Alicate universal", "AU-001"),
            SeedTool(ToolType.HAMMER, "Martillo de goma", "MG-001"),
            SeedTool(ToolType.HAMMER, "Martillo de bola", "MB-001"),
            SeedTool(ToolType.HAMMER, "Martillo de uña", "MU-001"),
            SeedTool(ToolType.SAW, "Sierra manual", "SM-001"),
            SeedTool(ToolType.SAW, "Sierra caladora", "SC-001"),
            SeedTool(ToolType.MEASURING, "Flexómetro 5m", "FM-005"),
            SeedTool(ToolType.MEASURING, "Calibrador vernier", "CV-001"),
            SeedTool(ToolType.MEASURING, "Micrómetro 0-25mm", "MC-025"),
            SeedTool(ToolType.MEASURING, "Nivel de burbuja", "NB-001"),
            SeedTool(ToolType.MEASURING, "Cinta métrica 30m", "CM-030"),
            SeedTool(ToolType.OTHER, "Cinta aislante", "CA-001"),
            SeedTool(ToolType.OTHER, "Lubricante multipropósito", "LM-001"),
            SeedTool(ToolType.OTHER, "Linterna LED", "LL-001"),
            SeedTool(ToolType.OTHER, "Cuchillo multiusos", "CM-001"),
            SeedTool(ToolType.OTHER, "Lima plana 200mm", "LF-200"),
        )

        for (tool in tools) {
            try {
                db.execSQL(
                    """
                    INSERT INTO ${ToolEntity.TABLE_NAME} 
                        (${ToolEntity.COLUMN_TYPE}, ${ToolEntity.COLUMN_NAME}, ${ToolEntity.COLUMN_CODE}, 
                         ${ToolEntity.COLUMN_BODY_MEDIA_URI}, ${ToolEntity.COLUMN_SYMBOL_MEDIA_URI}) 
                    VALUES (?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf(tool.type.name, tool.name, tool.code, null, null)
                )
            } catch (_: SQLiteConstraintException) {
                // skip duplicates
            }
        }
    }
}
