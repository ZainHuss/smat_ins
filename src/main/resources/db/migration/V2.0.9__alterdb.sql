alter table sticker  add for_user bigint DEFAULT NULL;

ALTER TABLE sticker
ADD CONSTRAINT FK_sticker_for_user FOREIGN KEY (for_user)
REFERENCES sys_user (id) ON DELETE NO ACTION;

ALTER TABLE inspection_form_workflow 
  ADD CONSTRAINT FK_inspection_form_workflow_reviewed_by FOREIGN KEY (reviewed_by)
    REFERENCES sys_user(id) ON DELETE NO ACTION;

