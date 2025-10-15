CREATE TABLE emp_certification_type (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cert_name VARCHAR(100) NOT NULL,  -- e.g., "WORK AT HEIGHT"
  description  varchar(3000),
  validity_period_months INT
);

CREATE TABLE employee (
  id INT PRIMARY KEY AUTO_INCREMENT,
  company_id INT,
  full_name VARCHAR(100) NOT NULL,  -- e.g., "NISHANTH PERUSHOTHAM"
  id_number VARCHAR(50) NOT NULL,   -- e.g., "2468856634"
  nationality VARCHAR(50),
  date_of_birth DATE,
  FOREIGN KEY (company_id) REFERENCES company(id)
);

CREATE TABLE emp_certification (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cert_type_id INT,
  employee_id INT,
  issue_date DATE NOT NULL,         -- e.g., "19-03-2024"
  expiry_date DATE NOT NULL,        -- e.g., "19-03-2025"
  cert_number VARCHAR(50) UNIQUE,   -- e.g., "SMI/24RD/ID/1766799"
  ts_number VARCHAR(50),            -- e.g., "00275"
  status ENUM('active', 'expired', 'revoked') DEFAULT 'active',
  issued_by bigint,                    -- User ID who issued it
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (cert_type_id) REFERENCES emp_certification_type(id),
  FOREIGN KEY (employee_id) REFERENCES employee(id),
  FOREIGN KEY (issued_by) REFERENCES sys_user(id)
);

CREATE TABLE equipment (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cert_type_id INT,
  equipment_name VARCHAR(100) NOT NULL,
  description  varchar(3000),
  FOREIGN KEY (cert_type_id) REFERENCES emp_certification_type(id)
);

CREATE TABLE emp_certification_equipment (
  cert_id INT,
  equipment_id INT,
  PRIMARY KEY (cert_id, equipment_id),
  FOREIGN KEY (cert_id) REFERENCES emp_certification(id),
  FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);

CREATE TABLE emp_certification_workflow (
  id int NOT NULL AUTO_INCREMENT,
  cert_id int NOT NULL,
  workflow_definition int NOT NULL,
  task int NOT NULL,
  reviewed_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (cert_id) REFERENCES emp_certification(id),
  FOREIGN KEY (workflow_definition) REFERENCES workflow_definition(id),
  FOREIGN KEY (task) REFERENCES task(id),
  FOREIGN KEY (reviewed_by) REFERENCES sys_user(id)
);

CREATE TABLE emp_certification_workflow_step (
  id bigint NOT NULL AUTO_INCREMENT,
  cert_id int NOT NULL,
  workflow_definition int NOT NULL,
  sys_user_comment varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  sys_user bigint NOT NULL,
  process_date date NOT NULL,
  step_seq smallint NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (cert_id) REFERENCES emp_certification(id),
  FOREIGN KEY (workflow_definition) REFERENCES workflow_definition(id),
  FOREIGN KEY (sys_user) REFERENCES sys_user(id)
);