package com.smat.ins.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class CertificatePdfGenerator {

    public static byte[] generate(Map<String, Object> parameters, ServletContext ctx) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            com.itextpdf.text.BaseColor brandBlue = new com.itextpdf.text.BaseColor(10, 102, 204);
            com.itextpdf.text.BaseColor lightGray = new com.itextpdf.text.BaseColor(250, 250, 252);
            com.itextpdf.text.BaseColor darkText = new com.itextpdf.text.BaseColor(38, 50, 56);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, com.itextpdf.text.BaseColor.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, brandBlue);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, darkText);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, darkText);
            Font urlFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, brandBlue);
            Font certLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, brandBlue);
            Font certValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, darkText);
            Font approvalLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, brandBlue);
            Font approvalValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, darkText);

            float pageW = document.getPageSize().getWidth();
            float pageH = document.getPageSize().getHeight();
            float margin = 28f;
            float gap = 18f;

            float panelW = pageW - margin * 2;
            float panelH = (pageH - margin * 2 - gap) / 2f;
            float cornerRadius = 8f;

            PdfContentByte cb = writer.getDirectContent();

            java.util.function.BiConsumer<Float, Float> drawPanelBg = (x, y) -> {
                try {
                    cb.saveState();
                    cb.setColorFill(new com.itextpdf.text.BaseColor(220, 220, 224));
                    cb.roundRectangle(x + 3f, y - 3f, panelW, panelH, cornerRadius);
                    cb.fill();
                    cb.restoreState();

                    cb.saveState();
                    cb.setColorFill(lightGray);
                    cb.roundRectangle(x, y, panelW, panelH, cornerRadius);
                    cb.fillStroke();
                    cb.restoreState();
                } catch (Exception ignore) {}
            };

            float frontX = margin;
            float frontY = pageH - margin - panelH;
            drawPanelBg.accept(frontX, frontY);

            float headerH = 110f;
            try {
                String leftPath = parameters.get("logoLeftPath") != null ? String.valueOf(parameters.get("logoLeftPath")) : ctx.getRealPath("views/jasper/images/logo-left.png");
                if (leftPath != null) {
                    byte[] leftBytes = makeImageTransparent(leftPath);
                    if (leftBytes != null) {
                        Image logoL = Image.getInstance(leftBytes);
                        logoL.scaleToFit(90, 90);
                        logoL.setAbsolutePosition(frontX + 12, frontY + panelH - 6 - logoL.getScaledHeight());
                        cb.addImage(logoL);
                    }
                }
            } catch (Exception ignore) {}

            try {
                String rightPath = parameters.get("logoRightPath") != null ? String.valueOf(parameters.get("logoRightPath")) : ctx.getRealPath("views/jasper/images/logo-right.png");
                if (rightPath != null) {
                    byte[] rightBytes = makeImageTransparent(rightPath);
                    if (rightBytes != null) {
                        Image logoR = Image.getInstance(rightBytes);
                        logoR.scaleToFit(160, 80);
                        logoR.setAbsolutePosition(frontX + panelW - 12 - logoR.getScaledWidth(), frontY + panelH - 6 - logoR.getScaledHeight());
                        cb.addImage(logoR);
                    }
                }
            } catch (Exception ignore) {}

            cb.setLineWidth(3f);
            cb.setColorStroke(brandBlue);
            cb.moveTo(frontX + 12f, frontY + panelH - headerH + 6f);
            cb.lineTo(frontX + panelW - 12f, frontY + panelH - headerH + 6f);
            cb.stroke();

            String certType = parameters.get("CertType") != null ? String.valueOf(parameters.get("CertType")) : "";
            float certifiedY = frontY + panelH - headerH - 22f;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Certified As:", certLabelFont), frontX + 12f, certifiedY, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(certType, certValueFont), frontX + 150f, certifiedY, 0);

            float photoX = frontX + 18f;
            float photoW = 120f; float photoH = 140f;
            float contentTop = frontY + panelH - headerH - 40f;
            float photoY = contentTop - photoH - 12f;

            try {
                cb.saveState();
                cb.setColorFill(new com.itextpdf.text.BaseColor(255, 255, 255));
                cb.setColorStroke(brandBlue);
                cb.setLineWidth(1f);
                cb.roundRectangle(photoX - 4f, photoY - 4f, photoW + 8f, photoH + 8f, 6f);
                cb.fillStroke();
                cb.restoreState();
            } catch (Exception ignore) {}

            try {
                Object empPhoto = parameters.get("EmployeePhoto");
                Image img = null;
                if (empPhoto instanceof java.io.InputStream) img = Image.getInstance(streamToBytes((java.io.InputStream) empPhoto));
                else if (empPhoto instanceof byte[]) img = Image.getInstance((byte[]) empPhoto);
                if (img != null) {
                    img.scaleToFit(photoW - 8f, photoH - 8f);
                    float imgW = img.getScaledWidth();
                    float imgH = img.getScaledHeight();
                    float imgX = photoX + (photoW - imgW) / 2f;
                    float imgY = photoY + (photoH - imgH) / 2f;
                    img.setAbsolutePosition(imgX, imgY);
                    cb.addImage(img);
                }
            } catch (Exception ignore) {}

            float infoX = frontX + photoW + 36f;
            float infoY = photoY + 4f;
            float infoW = frontX + panelW - 18f - infoX;
            float infoH = photoH + 8f;

            cb.saveState();
            cb.setColorFill(brandBlue);
            cb.roundRectangle(infoX, infoY + infoH - 26f, infoW, 26f, 8f);
            cb.fill();
            cb.restoreState();

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Company: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, com.itextpdf.text.BaseColor.WHITE)), infoX + 8f, infoY + infoH - 18f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(parameters.get("CompanyName") != null ? String.valueOf(parameters.get("CompanyName")) : "", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, com.itextpdf.text.BaseColor.WHITE)), infoX + 80f, infoY + infoH - 18f, 0);

            float innerPad = 8f;
            float availableH = infoH - 36f;
            int rowsCount = 4;
            float rowH = availableH / rowsCount;
            float curY = infoY + infoH - 36f;
            String[][] rows = new String[][] {
                    {"Name:", parameters.get("EmployeeName") != null ? String.valueOf(parameters.get("EmployeeName")) : ""},
                    {"Id/Iqama No:", parameters.get("EmployeeId") != null ? String.valueOf(parameters.get("EmployeeId")) : ""},
                    {"Issue on:", formatDateParam(parameters.get("IssueDate"))},
                    {"Valid up to:", formatDateParam(parameters.get("ExpiryDate"))}
            };
            cb.setLineWidth(1f);
            cb.setColorStroke(brandBlue);
            for (int i = 0; i < rows.length; i++) {
                float rY = curY - rowH + 6f;
                cb.saveState();
                cb.setColorFill(new com.itextpdf.text.BaseColor(255, 255, 255));
                cb.rectangle(infoX, rY, infoW, rowH - 6f);
                cb.fillStroke();
                cb.restoreState();

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(rows[i][0], labelFont), infoX + innerPad, rY + (rowH / 2f) - 6f, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(rows[i][1], valueFont), infoX + 110f, rY + (rowH / 2f) - 6f, 0);
                curY -= rowH;
            }

            float bottomY = frontY + 22f;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("ID No.", labelFont), frontX + 12f, bottomY, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("ID/" + (parameters.get("CertNumber") != null ? String.valueOf(parameters.get("CertNumber")) : ""), approvalValueFont), frontX + 62f, bottomY, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase("TS No.", labelFont), frontX + panelW - 92f, bottomY, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(parameters.get("TsNumber") != null ? String.valueOf(parameters.get("TsNumber")) : "", approvalValueFont), frontX + panelW - 12f, bottomY, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Certified to Operate the equipment listed on the back side of the card", smallFont), frontX + panelW / 2f, frontY + 6f, 0);

            float backX = frontX;
            float backY = frontY - gap - panelH;
            drawPanelBg.accept(backX, backY);

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("This card is issued by and remains the property of:", smallFont), backX + panelW / 2f, backY + panelH - 16f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("SMAT for Inspection Company", titleFont), backX + panelW / 2f, backY + panelH - 44f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Postal code: 34432, P.O.Box: 19331", smallFont), backX + panelW / 2f, backY + panelH - 62f, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("ID No. " + "ID/" + (parameters.get("CertNumber") != null ? String.valueOf(parameters.get("CertNumber")) : ""), valueFont), backX + panelW / 2f, backY + panelH - 74f, 0);

            float boxX = backX + 28f;
            float boxY = backY + 12f;
            float boxW = panelW - 56f;
            float boxH = panelH - 180f;

            cb.setLineWidth(1f);
            cb.setColorStroke(brandBlue);
            cb.rectangle(boxX, boxY, boxW, boxH);
            cb.stroke();

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("APPROVAL SCHEDULE", approvalLabelFont), boxX + 12f, boxY + boxH - 34f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(certType, approvalValueFont), boxX + 12f, boxY + boxH - 58f, 0);

            try {
                byte[] qrBytes = null;
                Object qobj = parameters.get("QRCodeImage");
                if (qobj instanceof java.io.InputStream) qrBytes = streamToBytes((java.io.InputStream) qobj);
                else if (qobj instanceof byte[]) qrBytes = (byte[]) qobj;
                if (qrBytes != null) {
                    Image q = Image.getInstance(qrBytes);
                    float qSize = Math.min(140f, boxH - 30f);
                    q.scaleToFit(qSize, qSize);
                    float qY = boxY + boxH - 12f - q.getScaledHeight();
                    float qX = boxX + boxW - 12f - q.getScaledWidth();
                    q.setAbsolutePosition(qX, qY);
                    cb.addImage(q);
                    float urlY = qY - 12f;
                    float urlX = qX + (q.getScaledWidth() / 2f);
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("www.smat-ins.com", urlFont), urlX, urlY, 0);
                }
            } catch (Exception ignore) {}

            document.close();
            return baos.toByteArray();
        } catch (DocumentException de) {
            throw new Exception("PDF generation failed: " + de.getMessage(), de);
        } finally {
            try { document.close(); } catch (Exception ignore) {}
        }
    }

    private static byte[] streamToBytes(java.io.InputStream is) throws java.io.IOException {
        if (is == null) return null;
        try (java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int r;
            while ((r = is.read(buffer)) != -1) bos.write(buffer, 0, r);
            return bos.toByteArray();
        }
    }

    private static byte[] makeImageTransparent(String path) {
        if (path == null) return null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            BufferedImage src = ImageIO.read(f);
            if (src == null) return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
            int w = src.getWidth();
            int h = src.getHeight();
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int rgb = src.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r > 240 && g > 240 && b > 240) {
                        dst.setRGB(x, y, (0 << 24) | (r << 16) | (g << 8) | b);
                    } else {
                        dst.setRGB(x, y, (255 << 24) | (r << 16) | (g << 8) | b);
                    }
                }
            }
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                ImageIO.write(dst, "png", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            try { return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)); } catch (Exception ex) { return null; }
        }
    }

    private static String formatDateParam(Object o) {
        if (o == null) return "";
        try {
            if (o instanceof java.util.Date) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                return sdf.format((java.util.Date) o);
            } else {
                return String.valueOf(o);
            }
        } catch (Exception e) { return String.valueOf(o); }
    }
}
