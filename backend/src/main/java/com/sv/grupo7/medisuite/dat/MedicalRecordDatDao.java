package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.model.medical.MedicalRecord;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordDatDao extends DatFileDao<MedicalRecord> {

    public MedicalRecordDatDao() {
        super("medical_records_backup.dat");
    }
}
