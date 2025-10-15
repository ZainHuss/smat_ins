
SET autocommit=0; 
SET unique_checks=0; 
SET foreign_key_checks=0;
INSERT INTO `checklist_data_source` (`id`, `name`, `description`, `code`) VALUES
	(1, 'general_data_source', 'general_data_source', '001'),
	(2, 'relevant_standard', 'relevant_standard', '002'),
	(3, 'choice_yes_no_ds', 'yes or no choice datasource', '003'),
	(4, 'choice_pass_or_fail_ds', 'pass or fail choice datasource', '004');

-- Dumping data for table smat_ins_db.checklist_detail_data_source: ~6 rows (approximately)
INSERT INTO `checklist_detail_data_source` (`id`, `checklist_data_source`, `item_value`, `item_code`) VALUES
	(1, 1, 'Satisfactory', '001'),
	(2, 1, 'Not Satisfactory', '002'),
	(3, 2, 'ASME B30.22-2023', '003'),
	(4, 2, 'ASME B30.10 2019', '004'),
	(5, 3, 'yes', '005'),
	(6, 3, 'no', '006'),
	(7, 4, 'pass', '007'),
	(8, 4, 'fail', '008');

INSERT INTO smat_ins_db.workflow(arabic_name, english_name, code) VALUES
('equipment inspection workflow', 'equipment inspection workflow', '001');

INSERT INTO smat_ins_db.workflow(arabic_name, english_name, code) VALUES
('employee training workflow', 'employee training workflow', '002');



INSERT INTO smat_ins_db.service_type (arabic_name, english_name, code, arabic_description, english_description)VALUES 
('فحص آلة', 'Equipment Inspection', '001', 'فحص آلة ومنح شهادة', 'Equipment Inspection then grant certificate to it');

INSERT INTO smat_ins_db.equipment_category(arabic_name, english_name, arabic_description, english_description, code) VALUES
('Lifting Equipment Of Boom Crane with Bucket', 'Lifting Equipment Of Boom Crane with Bucket', 'Lifting Equipment Of Boom Crane with Bucket', 'Lifting Equipment Of Boom Crane with Bucket', '001');
INSERT INTO smat_ins_db.equipment_category(arabic_name, english_name, arabic_description, english_description, code) VALUES
('Earth Moving Machinery Of EXCAVATOR', 'Earth Moving Machinery Of EXCAVATOR', 'Earth Moving Machinery Of EXCAVATOR', 'Earth Moving Machinery Of EXCAVATOR', '002');
INSERT INTO smat_ins_db.equipment_category(arabic_name, english_name, arabic_description, english_description, code) VALUES
('Lifting Equipment Of Boom Crane', 'Lifting Equipment Of Boom Crane', 'Lifting Equipment Of Boom Crane', 'Lifting Equipment Of Boom Crane', '003');

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