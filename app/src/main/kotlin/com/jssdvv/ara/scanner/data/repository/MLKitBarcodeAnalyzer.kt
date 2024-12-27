package com.jssdvv.ara.scanner.data.repository

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.jssdvv.ara.scanner.domain.repository.BarcodeAnalyzerRepository

class MLKitBarcodeAnalyzer(
    private val barcode: (barcode: Barcode, width: Int, height: Int) -> Unit
) : BarcodeAnalyzerRepository {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAllPotentialBarcodes()
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees
            ).let { inputImage ->
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            barcodes.firstOrNull().let { barcode ->
                                if (barcode != null) {
                                    barcode(barcode, inputImage.width, inputImage.height)
                                }
                            }
                        }
                    }.addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }.addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
    }
}