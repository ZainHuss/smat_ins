package com.smat.ins.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smat.ins.model.entity.EquipmentInspectionCertificate;
import com.smat.ins.model.service.EquipmentInspectionCertificateService;

@RestController
@RequestMapping("/api")
public class EquipmentCertificateExport {
	
	private final EquipmentInspectionCertificateService equipmentInspectionCertificateService;

    @Autowired
    public EquipmentCertificateExport(EquipmentInspectionCertificateService equipmentInspectionCertificateService) {
        this.equipmentInspectionCertificateService=equipmentInspectionCertificateService;
    }

    @GetMapping("/equipment-cert")
    public ResponseEntity<byte[]> getCertificate(@RequestParam(name = "serialNo",required = true) String serialNo,@RequestParam(name = "stickerNo",required = true) String stickerNo) {
        EquipmentInspectionCertificate certificate = equipmentInspectionCertificateService.getBy(serialNo, stickerNo);
        if (certificate == null || certificate.getCertificateDocument() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "certificate-" + serialNo+"&"+stickerNo + ".pdf");
        headers.setContentLength(certificate.getCertificateDocument().length);

        return new ResponseEntity<>(certificate.getCertificateDocument(), headers, HttpStatus.OK);
    }

}
