
SET autocommit=0; 
SET unique_checks=0; 
SET foreign_key_checks=0;
INSERT INTO step (arabic_name
, english_name
, code)
  VALUES ('مثبت', 'fixed', '03');
  
INSERT INTO smat_ins_db.workflow_definition(id, workflow, step, previous, next, initial_step, final_step) VALUES
(1, 1, 1, NULL, 2, True, NULL);
INSERT INTO smat_ins_db.workflow_definition(id, workflow, step, previous, next, initial_step, final_step) VALUES
(2, 1, 2, 1, 3, NULL, NULL);
INSERT INTO smat_ins_db.workflow_definition(id, workflow, step, previous, next, initial_step, final_step) VALUES
(3, 1, 3, NULL, NULL, NULL, True);
INSERT INTO smat_ins_db.workflow_definition(id, workflow, step, previous, next, initial_step, final_step) VALUES
(4, 2, 1, NULL, 5, True, NULL);
INSERT INTO smat_ins_db.workflow_definition(id, workflow, step, previous, next, initial_step, final_step) VALUES
(5, 2, 2, 4, 6, NULL, NULL);
INSERT INTO smat_ins_db.workflow_definition(id, workflow, step, previous, next, initial_step, final_step) VALUES
(6, 2, 3, NULL, NULL, NULL, True);
  
  
SET autocommit=1; 
SET unique_checks=1; 
SET foreign_key_checks=1;