package com.jssdvv.ara.scanner.data.repository

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.jssdvv.ara.scanner.domain.repository.BarcodeAnalyzerRepository

class MLKitBarcodeAnalyzer(
    private val barcodes: (
        barcodes: List<Barcode>,
        width: Int,
        height: Int,
        rotationDegrees: Int,
    ) -> Unit,
) : BarcodeAnalyzerRepository {

    private val scannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        //.enableAllPotentialBarcodes() // Keeps bounds for all potential barcodes
        .build()

    private val scanner = BarcodeScanning.getClient(scannerOptions)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image ?: return
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes(barcodes, inputImage.width, inputImage.height, rotationDegrees)
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }
}