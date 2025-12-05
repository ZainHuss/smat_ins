INSERT INTO smat_ins_db.organization(id, parent_organization, arabic_name, english_name, code, prefix_code, suffix_code, is_divan, has_private_hierarchy, hierarchy_system, hierarchy_system_def) VALUES
(1, NULL, 'شركة سمات', 'Smat-INS', '001', 'smat', 'sa', True, True, 1, 1);

INSERT INTO smat_ins_db.sys_role(id, name, code, description) VALUES
    (1, 'Inspector Role', '001', 'Inspector role')
    ON DUPLICATE KEY UPDATE id=VALUES(id), name=VALUES(name), code=VALUES(code), description=VALUES(description);
INSERT INTO smat_ins_db.sys_role(id, name, code, description) VALUES
    (2, 'Reviewer Role', '002', 'Reviewer Role')
    ON DUPLICATE KEY UPDATE id=VALUES(id), name=VALUES(name), code=VALUES(code), description=VALUES(description);
INSERT INTO smat_ins_db.sys_role(id, name, code, description) VALUES
    (3, 'Sales Role', '003', 'Sales Role')
    ON DUPLICATE KEY UPDATE id=VALUES(id), name=VALUES(name), code=VALUES(code), description=VALUES(description);
INSERT INTO smat_ins_db.sys_role(id, name, code, description) VALUES
    (4, 'Coordinator Role', '004', 'Coordinator user assign Tasks')
    ON DUPLICATE KEY UPDATE id=VALUES(id), name=VALUES(name), code=VALUES(code), description=VALUES(description);

INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (1, 4, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (2, 5, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (4, 10, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (5, 12, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (6, 3, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (7, 6, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (8, 1, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (9, 2, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (10, 9, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (11, 13, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (13, 4, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (14, 13, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (15, 5, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (17, 9, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (19, 11, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (20, 1, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (22, 2, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (23, 3, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (25, 12, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (26, 4, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (27, 5, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (28, 12, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (29, 15, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (30, 16, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (31, 14, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (32, 17, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);