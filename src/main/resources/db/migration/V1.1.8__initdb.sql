INSERT INTO smat_ins_db.organization(id, parent_organization, arabic_name, english_name, code, prefix_code, suffix_code, is_divan, has_private_hierarchy, hierarchy_system, hierarchy_system_def) VALUES
(1, NULL, 'شركة سمات', 'Smat-INS', '001', 'smat', 'sa', True, True, 1, 1);

INSERT INTO smat_ins_db.sys_role(id, name, code, description) VALUES
(1, 'Inspector role', '001', 'Inspector role');
INSERT INTO smat_ins_db.sys_role(id, name, code, description) VALUES
(2, 'Reviewer role', '002', NULL);

INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(1, 4, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(2, 5, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(3, 8, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(4, 10, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(5, 12, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(6, 3, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(7, 6, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(8, 1, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(9, 2, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(10, 9, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(11, 13, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(12, 7, 1);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(13, 4, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(14, 13, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(15, 5, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(16, 6, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(17, 9, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(18, 7, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(19, 11, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(20, 1, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(21, 8, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(22, 2, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(23, 3, 2);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
(24, 12, 2);