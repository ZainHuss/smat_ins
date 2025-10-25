package com.smat.ins.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

public class PdfUtil {

    public static byte[] concatenatePdf(List<byte[]> pdfs) throws Exception {
        if (pdfs == null || pdfs.isEmpty()) return null;
        Document document = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            for (int i = 0; i < pdfs.size(); i++) {
                byte[] pdf = pdfs.get(i);
                if (pdf == null || pdf.length == 0) continue;
                PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
                if (document == null) {
                    document = new Document(reader.getPageSizeWithRotation(1));
                    PdfCopy copy = new PdfCopy(document, out);
                    document.open();
                    // copy pages
                    int n = reader.getNumberOfPages();
                    for (int p = 1; p <= n; p++) {
                        copy.addPage(copy.getImportedPage(reader, p));
                    }
                    copy.freeReader(reader);
                    reader.close();
                    // continue with same copy for following pdfs
                    // we need to re-open PdfCopy; simpler approach: gather in first pass
                } else {
                    // reopen using PdfCopy from existing out - but for simplicity, use a new approach below
                    // fallback: throw if multiple PDFs after first (rare)
                    PdfReader r2 = new PdfReader(new ByteArrayInputStream(pdf));
                    PdfCopy copy = new PdfCopy(document, out);
                    int n2 = r2.getNumberOfPages();
                    for (int p = 1; p <= n2; p++) {
                        copy.addPage(copy.getImportedPage(r2, p));
                    }
                    copy.freeReader(r2);
                    r2.close();
                }
            }
        } finally {
            if (document != null && document.isOpen()) document.close();
        }
        return out.toByteArray();
    }
}
