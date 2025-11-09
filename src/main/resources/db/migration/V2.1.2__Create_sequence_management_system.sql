-- +FlywayNonTransactional

-- =============================================
-- نظام إدارة التسلسل والحجز
-- =============================================

-- 1. جداول التسلسل الرئيسية
CREATE TABLE equipment_inspection_seq (
                                          equipment_cat_code varchar(50) NOT NULL,
                                          last_val int NOT NULL,
                                          PRIMARY KEY (equipment_cat_code)
) ENGINE=INNODB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE emp_cert_seq (
                              seq_name varchar(50) NOT NULL,
                              last_val int NOT NULL,
                              PRIMARY KEY (seq_name)
) ENGINE=INNODB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 2. جداول الحجز
CREATE TABLE equipment_inspection_seq_reservation (
                                                      id bigint NOT NULL AUTO_INCREMENT,
                                                      equipment_cat_code varchar(50) NOT NULL,
                                                      reserved_val int NOT NULL,
                                                      task_id bigint NOT NULL,
                                                      reserved_by bigint DEFAULT NULL,
                                                      reserved_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                      status enum('reserved','confirmed','released') NOT NULL DEFAULT 'reserved',
                                                      PRIMARY KEY (id)
) ENGINE=INNODB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE emp_cert_seq_reservation (
                                          id bigint NOT NULL AUTO_INCREMENT,
                                          seq_name varchar(50) NOT NULL,
                                          reserved_val int NOT NULL,
                                          task_id bigint NOT NULL,
                                          reserved_by bigint DEFAULT NULL,
                                          reserved_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          status enum('reserved','confirmed','released') NOT NULL DEFAULT 'reserved',
                                          PRIMARY KEY (id)
) ENGINE=INNODB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 3. الفهارس
ALTER TABLE equipment_inspection_seq_reservation
    ADD INDEX idx_equipment_cat_val (equipment_cat_code, reserved_val),
ADD UNIQUE INDEX uq_equipment_res_task (task_id);

ALTER TABLE emp_cert_seq_reservation
    ADD INDEX idx_emp_seq_val (seq_name, reserved_val),
ADD UNIQUE INDEX uq_emp_cert_res_task (task_id);

-- 4. البيانات الأولية
INSERT INTO equipment_inspection_seq (equipment_cat_code, last_val) VALUES
                                                                        ('CRANE', 0), ('LIFT', 0), ('BOOM', 0), ('SCAFFOLD', 0), ('GENERATOR', 0);

INSERT INTO emp_cert_seq (seq_name, last_val) VALUES
                                                  ('TECHNICIAN', 0), ('INSPECTOR', 0), ('ENGINEER', 0), ('SUPERVISOR', 0), ('OPERATOR', 0);