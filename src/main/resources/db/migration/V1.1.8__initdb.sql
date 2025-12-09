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
    (4, 10, 1)
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
    (25, 12, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (26, 4, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (27, 5, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (47, 4, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (59, 4, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (69, 9, 3)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (70, 14, 3)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (71, 15, 3)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (72, 16, 3)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (76, 17, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (77, 15, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (78, 14, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (79, 16, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (80, 9, 1)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (81, 14, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (82, 15, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (83, 16, 2)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (84, 16, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (85, 15, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (86, 14, 4)
    ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (87, 9, 4)
INSERT INTO smat_ins_db.sys_role_permission(id, sys_permission, sys_role) VALUES
    (88, 18, 4)
ON DUPLICATE KEY UPDATE id=VALUES(id), sys_permission=VALUES(sys_permission), sys_role=VALUES(sys_role);
