package com.smat.ins.web;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.smat.ins.model.entity.Sticker;
import com.smat.ins.model.service.StickerService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.UtilityHelper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/export/stickersRange")
public class ExportStickersRangeServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ExportStickersRangeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        if (from == null || to == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing from/to parameters");
            return;
        }

        try {
            ServletContext servletCtx = req.getServletContext();
            StickerService stickerService = (StickerService) BeanUtility.getBean(servletCtx, "stickerService");
            List<Sticker> all = stickerService.findAll();

            int fromNum = extractNumericSuffix(from);
            int toNum = extractNumericSuffix(to);
            if (fromNum < 0 || toNum < 0 || fromNum > toNum) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid range");
                return;
            }

            List<Sticker> range = all.stream()
                    .filter(s -> s.getStickerNo() != null)
                    .filter(s -> {
                        int v = extractNumericSuffix(s.getStickerNo());
                        return v >= fromNum && v <= toNum;
                    })
                    .sorted(Comparator.comparingLong(s -> s.getSeq() == null ? 0L : s.getSeq()))
                    .collect(Collectors.toList());

            if (range.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No stickers found in the selected range");
                return;
            }

                // compute base URL without relying on FacesContext
                String scheme = req.getScheme();
                String serverName = req.getServerName();
                int serverPort = req.getServerPort();
                String contextPath = req.getContextPath();
                String baseUrl = scheme + "://" + serverName + ((serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort) + contextPath;

                List<Map<String, Object>> dataSource = new ArrayList<>();
            for (Sticker sticker : range) {
                Map<String, Object> record = new HashMap<>();
                record.put("stickerNo", sticker.getStickerNo());
                String qrText = baseUrl + "/api/equipment-cert/" + (sticker.getSerialNo() != null ? sticker.getSerialNo() : "") + "&"
                    + (sticker.getStickerNo() != null ? sticker.getStickerNo() : "");
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 100, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                record.put("smatQrCode", baos.toByteArray());
                dataSource.add(record);
            }

            Map<String, Object> parameters = new HashMap<>();
            ServletContext ctx = req.getServletContext();
            parameters.put("lstSticker", dataSource);
            parameters.put("logoLeftPath", ctx.getRealPath("/views/jasper/images/logo-left.png"));
            parameters.put("logoRightPath", ctx.getRealPath("/views/jasper/images/logo-right.png"));
            parameters.put("iconMailPath", ctx.getRealPath("/views/jasper/images/icon-mail.png"));
            parameters.put("iconMobilePath", ctx.getRealPath("/views/jasper/images/icon-mobile.png"));
            parameters.put("iconSitePath", ctx.getRealPath("/views/jasper/images/icon-site.png"));
            parameters.put("iconWhatsPath", ctx.getRealPath("/views/jasper/images/icon-whats.png"));

            String jasperPath = ctx.getRealPath("/views/jasper/Sticker.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, new JRBeanCollectionDataSource(dataSource));
            byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);

            resp.setContentType("application/pdf");
            resp.setHeader("Content-disposition", "inline; filename=stickers_" + System.currentTimeMillis() + ".pdf");
            resp.setContentLength(pdf.length);
            try (ServletOutputStream sos = resp.getOutputStream()) {
                sos.write(pdf);
                sos.flush();
            }

        } catch (Exception e) {
            log.error("Failed to export sticker range", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Export failed");
        }
    }

    private int extractNumericSuffix(String stickerNo) {
        if (stickerNo == null) return -1;
        String digits = stickerNo.replaceAll("\\D", "");
        if (digits.isEmpty()) return -1;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }
}
