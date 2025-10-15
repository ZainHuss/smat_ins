alter table inspection_form_workflow ADD task int NOT NULL;

--
-- Create foreign key
--
ALTER TABLE inspection_form_workflow
ADD CONSTRAINT FK_inspection_form_workflow_task FOREIGN KEY (task)
REFERENCES task (id);