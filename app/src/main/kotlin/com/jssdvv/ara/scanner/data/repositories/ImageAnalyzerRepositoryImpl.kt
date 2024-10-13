package com.jssdvv.ara.scanner.data.repositories

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.jssdvv.ara.scanner.domain.ImageAnalyzerRepository

class ImageAnalyzerRepositoryImpl(
    private val onBarcodesDetected: (barcodes: MutableList<Barcode>) -> Unit
) : ImageAnalyzerRepository {
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAllPotentialBarcodes()
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {

        imageProxy.image?.let { image ->

            val inputImage = InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    onBarcodesDetected(barcodes)
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                }.addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}