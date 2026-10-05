package com.decamanager.deca;

import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@Component
public class QrGenerator {

    private static final int TAMANO_PX = 300;

    public byte[] generar(String contenido) {
        try {
            Map<EncodeHintType, Object> opciones = new EnumMap<>(EncodeHintType.class);
            opciones.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            opciones.put(EncodeHintType.MARGIN, 1);

            BitMatrix matriz = new QRCodeWriter().encode(
                    contenido, BarcodeFormat.QR_CODE, TAMANO_PX, TAMANO_PX, opciones);

            try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
                ImageIO.write(MatrixToImageWriter.toBufferedImage(matriz), "PNG", salida);
                return salida.toByteArray();
            }
        } catch (WriterException | java.io.IOException e) {
            throw new IllegalStateException("No se pudo generar el código QR", e);
        }
    }
}
