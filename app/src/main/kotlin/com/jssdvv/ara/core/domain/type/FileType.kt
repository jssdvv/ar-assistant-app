package com.jssdvv.ara.core.domain.type

enum class FileType(
    val extensions: Set<String>,
    val mimeType: String,
    val shareSubject: String,
    val shareText: String,
) {
    MODEL(
        extensions = setOf("glb"),
        mimeType = "model/gltf-binary",
        shareSubject = "3D Model",
        shareText = "Sharing a 3D model file",
    ),
    VECTOR(
        extensions = setOf("svg"),
        mimeType = "image/svg+xml",
        shareSubject = "Vector Image",
        shareText = "Sharing a vector image file",
    ),
    IMAGE(
        extensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff"),
        mimeType = "image/*",
        shareSubject = "Image",
        shareText = "Sharing an image file",
    ),
    PDF(
        extensions = setOf("pdf"),
        mimeType = "application/pdf",
        shareSubject = "PDF Document",
        shareText = "Sharing a PDF document",
    );

    companion object {
        fun fromExtension(extension: String): FileType? =
            entries.firstOrNull { it.extensions.contains(extension) }
    }
}