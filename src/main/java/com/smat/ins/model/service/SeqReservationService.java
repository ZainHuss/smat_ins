package com.smat.ins.model.service;

public interface SeqReservationService {

    // Equipment report reservation
    Integer getReservedReportNoForTask(Integer taskId);

    Integer reserveReportNoForTask(Integer taskId, String equipmentCatCode, Long reservedBy);

    Boolean confirmReservedReportNoForTask(Integer taskId);

    Boolean releaseReservedReportNoForTask(Integer taskId);

    // Employee certificate reservation
    Integer getReservedCertNoForTask(Integer taskId);

    Integer reserveCertNoForTask(Integer taskId, Long reservedBy);

    Boolean confirmReservedCertNoForTask(Integer taskId);

    Boolean releaseReservedCertNoForTask(Integer taskId);

}
