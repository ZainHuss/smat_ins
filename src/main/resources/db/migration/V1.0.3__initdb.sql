
SET autocommit=0; 
SET unique_checks=0; 
SET foreign_key_checks=0;
INSERT INTO smat_ins_db.checklist_data_source(id, name, description, code) VALUES
    (1, 'general_data_source', 'general_data_source', '001');
INSERT INTO smat_ins_db.checklist_data_source(id, name, description, code) VALUES
    (2, 'relevant_standard', 'relevant_standard', '002');
INSERT INTO smat_ins_db.checklist_data_source(id, name, description, code) VALUES
    (3, 'choice_yes_no_ds', 'yes or no choice datasource', '003');
INSERT INTO smat_ins_db.checklist_data_source(id, name, description, code) VALUES
    (4, 'choice_pass_or_fail_ds', 'pass or fail choice datasource', '004');
INSERT INTO smat_ins_db.checklist_data_source(id, name, description, code) VALUES
    (5, 'S-NS-NA', '(Satisfactory-S/Not Satisfactory-NS/Not Applicable-NA)', '005');
INSERT INTO smat_ins_db.checklist_data_source(id, name, description, code) VALUES
    (6, 'Type of Crane  and Source of Power:', 'Type of Crane and Source of Power:', '006');

-- Dumping data for table smat_ins_db.checklist_detail_data_source: ~6 rows (approximately)
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (1, 1, 'Satisfactory', '001');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (2, 1, 'Not Satisfactory', '002');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (3, 2, 'ASME B30.22-2023', '003');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (4, 2, 'ASME B30.10 2019', '004');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (5, 3, 'yes', '005');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (6, 3, 'no', '006');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (7, 4, 'Pass', '007');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (8, 4, 'Fail', '008');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (9, 5, 'S', '009');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (10, 5, 'NS', '010');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (11, 5, 'NA', '011');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (12, 6, 'Rough Terrain Type, Single Cabin, Diesel Engine Power', '012');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (13, 6, 'All Terrain Type, Double Cabin, Diesel Engine Power', '013');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (14, 6, 'Truck Mounted Type, Double Cabin, Diesel Engine Power', '014');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (15, 6, 'Boom Truck Type, Controlled via lower control Station, Diesel Power Engine', '015');
INSERT INTO smat_ins_db.checklist_detail_data_source(id, checklist_data_source, item_value, item_code) VALUES
    (16, 6, 'Crawler Type, Single Cabin, Diesel Poweer Engine', '016');

INSERT INTO smat_ins_db.workflow(arabic_name, english_name, code) VALUES
('equipment inspection workflow', 'equipment inspection workflow', '001');

INSERT INTO smat_ins_db.workflow(arabic_name, english_name, code) VALUES
('employee training workflow', 'employee training workflow', '002');




INSERT INTO smat_ins_db.equipment_type(arabic_name, english_name, code) VALUES
('Articulating boom crane truck mounted, Diesel type', 'Articulating boom crane truck mounted, Diesel type', '001');
INSERT INTO smat_ins_db.equipment_type(arabic_name, english_name, code) VALUES
('Diesel Powered Excavator', 'Diesel Powered Excavator', '002');

INSERT INTO smat_ins_db.examination_type(arabic_name, english_name, code) VALUES
('Periodic', 'Periodic', '001');
INSERT INTO smat_ins_db.examination_type(arabic_name, english_name, code) VALUES
('Initial', 'Initial', '002');

SET autocommit=1; 
SET unique_checks=1; 
SET foreign_key_checks=1;