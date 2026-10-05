package com.decamanager.deca;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.decamanager.transporte.Transporte;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class PdfGenerator {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generar(Transporte transporte, byte[] imagenQr, String urlPublica) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.A4, 40, 40, 50, 40);
            PdfWriter.getInstance(documento, salida);
            documento.open();

            escribirCabecera(documento, transporte);
            escribirDatosTransporte(documento, transporte);
            escribirDatosMercancia(documento, transporte);
            escribirQr(documento, imagenQr, urlPublica);

            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF del DeCA", e);
        }
    }

    private void escribirCabecera(Document documento, Transporte transporte) throws Exception {
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph titulo = new Paragraph("Documento de Control Administrativo (DeCA)", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph referencia = new Paragraph("Transporte nº " + transporte.getId(), fuenteSubtitulo);
        referencia.setAlignment(Element.ALIGN_CENTER);
        referencia.setSpacingAfter(20);
        documento.add(referencia);
    }

    private void escribirDatosTransporte(Document documento, Transporte transporte) throws Exception {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(15);

        agregarFila(tabla, "Cargador",
                transporte.getCargador().getNombre() + " (NIF " + transporte.getCargador().getNif() + ")");
        agregarFila(tabla, "Transportista",
                transporte.getTransportista().getNombre() + " (NIF " + transporte.getTransportista().getNif() + ")");
        agregarFila(tabla, "Vehículo",
                transporte.getVehiculo().getMatricula() + " (" + transporte.getVehiculo().getTipo() + ")");
        agregarFila(tabla, "Fecha de la operación", transporte.getFechaOperacion().format(FORMATO_FECHA));
        agregarFila(tabla, "Lugar de carga", transporte.getLugarCarga());
        agregarFila(tabla, "Fecha de carga", transporte.getFechaCarga().format(FORMATO_FECHA));
        agregarFila(tabla, "Lugar de descarga", transporte.getLugarDescarga());
        agregarFila(tabla, "Fecha de descarga", transporte.getFechaDescarga().format(FORMATO_FECHA));

        documento.add(tabla);
    }

    private void escribirDatosMercancia(Document documento, Transporte transporte) throws Exception {
        Font fuenteApartado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        documento.add(new Paragraph("Mercancía transportada", fuenteApartado));

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(8);

        agregarFila(tabla, "Naturaleza", transporte.getMercanciaNaturaleza());
        agregarFila(tabla, "Peso/Volumen",
                transporte.getMercanciaPeso() + " " + transporte.getMercanciaUnidad());

        if (transporte.getNotas() != null && !transporte.getNotas().isBlank()) {
            agregarFila(tabla, "Notas", transporte.getNotas());
        }

        documento.add(tabla);
    }

    private void escribirQr(Document documento, byte[] imagenQr, String urlPublica) throws Exception {
        Image qr = Image.getInstance(imagenQr);
        qr.scaleToFit(120, 120);
        qr.setAlignment(Element.ALIGN_CENTER);
        qr.setSpacingBefore(20);
        documento.add(qr);

        Font fuenteUrl = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Paragraph url = new Paragraph(urlPublica, fuenteUrl);
        url.setAlignment(Element.ALIGN_CENTER);
        documento.add(url);
    }

    private void agregarFila(PdfPTable tabla, String etiqueta, String valor) {
        Font fuenteEtiqueta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font fuenteValor = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fuenteEtiqueta));
        celdaEtiqueta.setBorder(0);
        celdaEtiqueta.setPaddingBottom(6);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fuenteValor));
        celdaValor.setBorder(0);
        celdaValor.setPaddingBottom(6);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }
}
