package com.smat.ins.view.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smat.ins.model.entity.EquipmentInspectionCertificate;
import com.smat.ins.model.service.EquipmentInspectionCertificateService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@RequestScoped
@Named
public class CertificateViewerBean implements Serializable {

    private static final long serialVersionUID = 646260418670243026L;

    private static final Logger log = LoggerFactory.getLogger(CertificateViewerBean.class);

    private Long equipCertId;

    private StreamedContent file;

    private EquipmentInspectionCertificateService equipmentInspectionCertificateService;
    private LocalizationService localizationService;

    public CertificateViewerBean() {
        equipmentInspectionCertificateService = (EquipmentInspectionCertificateService) BeanUtility
                .getBean("equipmentInspectionCertificateService");
        localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
    }

    @PostConstruct
    public void init() {
        try {
            // 1) Try get from session
            Object sessVal = UtilityHelper.getSessionAttr("equipCertId");
            if (sessVal != null) {
                try {
                    if (sessVal instanceof Long) {
                        equipCertId = (Long) sessVal;
                    } else if (sessVal instanceof Integer) {
                        equipCertId = Long.valueOf((Integer) sessVal);
                    } else if (sessVal instanceof String) {
                        equipCertId = Long.valueOf(((String) sessVal).trim());
                    } else {
                        log.debug("CertificateViewerBean.init - session equipCertId has unexpected type: {}",
                                sessVal.getClass().getName());
                    }
                } catch (Exception e) {
                    log.warn("CertificateViewerBean.init - failed parsing session equipCertId: {}", sessVal, e);
                }
            }

            // 2) If not in session, try request parameter (common in links)
            if (equipCertId == null) {
                String idParam = UtilityHelper.getRequestParameter("equipCertId");
                if (idParam == null || idParam.trim().isEmpty()) {
                    // try alternative param name "id"
                    idParam = UtilityHelper.getRequestParameter("id");
                }
                if (idParam != null && !idParam.trim().isEmpty()) {
                    try {
                        // if you use encrypted ids in URLs, decipher here
                        try {
                            idParam = UtilityHelper.decipher(idParam);
                        } catch (Exception e) {
                            // if decipher fails, assume it's plain
                            log.debug("CertificateViewerBean.init - decipher not applied or failed: {}", e.getMessage());
                        }
                        equipCertId = Long.valueOf(idParam.trim());
                    } catch (NumberFormatException nfe) {
                        log.warn("CertificateViewerBean.init - invalid request param for equipCertId: {}", idParam, nfe);
                    }
                }
            }

            // If still null, bail out gracefully
            if (equipCertId == null) {
                log.warn("CertificateViewerBean.init - no equipCertId provided in session or request. Aborting init.");
                UtilityHelper.addErrorMessage(
                        localizationService != null ? localizationService.getInterfaceLabel().getString("fileNotFound")
                                : "Certificate not found");
                return;
            }

            // Fetch certificate defensively
            EquipmentInspectionCertificate equipmentInspectionCertificate = null;
            try {
                equipmentInspectionCertificate = equipmentInspectionCertificateService.findById(equipCertId);
            } catch (Exception e) {
                log.error("CertificateViewerBean.init - error calling findById for id: " + equipCertId, e);
            }

            if (equipmentInspectionCertificate == null) {
                log.warn("CertificateViewerBean.init - no certificate found for id: {}", equipCertId);
                UtilityHelper.addErrorMessage(
                        localizationService != null ? localizationService.getInterfaceLabel().getString("fileNotFound")
                                : "Certificate not found");
                return;
            }

            // Ensure document bytes exist
            byte[] docBytes = equipmentInspectionCertificate.getCertificateDocument();
            if (docBytes == null || docBytes.length == 0) {
                log.warn("CertificateViewerBean.init - certificate document is null/empty for id: {}", equipCertId);
                UtilityHelper.addErrorMessage(
                        localizationService != null ? localizationService.getInterfaceLabel().getString("fileNotFound")
                                : "Certificate file not found");
                return;
            }

            // Build streamed content safely
            InputStream inputStream = new ByteArrayInputStream(docBytes);
            String fileName = null;
            try {
                if (equipmentInspectionCertificate.getEquipmentInspectionForm() != null) {
                    fileName = equipmentInspectionCertificate.getEquipmentInspectionForm().getReportNo();
                }
            } catch (Exception e) {
                log.debug("CertificateViewerBean.init - couldn't determine file name from inspection form", e);
            }
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "certificate-" + equipCertId;
            }

            file = DefaultStreamedContent.builder().name(fileName).contentType("application/pdf")
                    .stream(() -> inputStream).build();

        } catch (Exception e) {
            // Catch-all to avoid NPEs bubbling to container
            log.error("CertificateViewerBean.init - unexpected error", e);
            e.printStackTrace();
            try {
                UtilityHelper.addErrorMessage(
                        localizationService != null ? localizationService.getInterfaceLabel().getString("fileNotFound")
                                : "Certificate not found");
            } catch (Exception ex) {
                // swallow secondary errors
                log.debug("CertificateViewerBean.init - error while showing message", ex);
            }
        }
    }

    public StreamedContent getFile() {
        return file;
    }

}
