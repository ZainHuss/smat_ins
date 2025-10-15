package com.smat.ins.util;

import com.aspose.barcode.generation.BarCodeImageFormat;
import com.aspose.barcode.generation.BarcodeGenerator;

import com.aspose.barcode.generation.EncodeTypes;
import com.aspose.barcode.generation.QRErrorLevel;

import com.aspose.barcode.generation.QrParameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeGenerator {

    /**
     * Generates a QR code image as a byte array
     *
     * @param text  The text content to encode in the QR code
     * @param scale The pixel size of each module (dot) in the QR code
     * @param border The border size around the QR code (in pixels)
     * @return Byte array containing the PNG image data
     * @throws IOException If an error occurs during QR code generation
     */
    public static byte[] generateQrCodeImage(String text, int scale, int border) throws IOException {
        try {
            // Create QR code generator with specified text
            BarcodeGenerator generator = new BarcodeGenerator(EncodeTypes.QR, text);
            
            // Configure QR code parameters
            QrParameters qrParams = generator.getParameters().getBarcode().getQR();
            
            // Set error correction level (LevelL = 7% recovery)
            qrParams.setQrErrorLevel(QRErrorLevel.LEVEL_L);
            
            // Set module size (size of each QR code dot)
            generator.getParameters().getBarcode().getXDimension().setPixels(scale);
            
            // Set padding around the QR code
            generator.getParameters().getBarcode().getPadding().getLeft().setPixels(border);
            generator.getParameters().getBarcode().getPadding().getRight().setPixels(border);
            generator.getParameters().getBarcode().getPadding().getTop().setPixels(border);
            generator.getParameters().getBarcode().getPadding().getBottom().setPixels(border);

            // Generate QR code image and return as byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            generator.save(outputStream, BarCodeImageFormat.PNG);
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new IOException("QR code generation failed: " + e.getMessage(), e);
        }
    }
}