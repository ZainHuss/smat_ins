ALTER TABLE equipment_inspection_form ADD inspection_by bigint DEFAULT NULL;
ALTER TABLE equipment_inspection_form ADD reviewed_by bigint DEFAULT NULL;
ALTER TABLE smat_ins_db.equipment_inspection_form
    ADD CONSTRAINT fk_eif_pinned_by FOREIGN KEY (pinned_by)
        REFERENCES smat_ins_db.sys_user (id);


--
-- Create foreign key
--
ALTER TABLE equipment_inspection_form
ADD CONSTRAINT FK_equipment_inspection_form_inspection_by FOREIGN KEY (inspection_by)
REFERENCES sys_user (id);

--
-- Create foreign key
--
ALTER TABLE equipment_inspection_form
ADD CONSTRAINT FK_equipment_inspection_form_reviewed_by FOREIGN KEY (reviewed_by)
REFERENCES sys_user (id);