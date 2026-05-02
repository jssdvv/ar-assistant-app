package com.jssdvv.ara.core.domain.type

enum class FileType(
    val extensions: Set<String>
) {
    MODEL(setOf("glb")),
    VECTOR(setOf("svg")),
    IMAGE(setOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff")),
    PDF(setOf("pdf")),
}