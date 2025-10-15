ALTER TABLE equipment_inspection_form ADD inspection_by bigint DEFAULT NULL;
ALTER TABLE equipment_inspection_form ADD reviewed_by bigint DEFAULT NULL;


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