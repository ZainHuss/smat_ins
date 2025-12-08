alter table emp_certification  ADD inspected_by bigint DEFAULT NULL;

alter table emp_certification  ADD reviewed_by bigint DEFAULT NULL;


ALTER TABLE emp_certification
ADD CONSTRAINT FK_emp_certification_inspected_by FOREIGN KEY (inspected_by)
REFERENCES smat_ins_db.sys_user (id);

ALTER TABLE emp_certification
ADD CONSTRAINT FK_emp_certification_reviewed_by FOREIGN KEY (reviewed_by)
REFERENCES smat_ins_db.sys_user (id);