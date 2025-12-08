CREATE TABLE printed_doc (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(400) DEFAULT NULL,
  extension varchar(50) DEFAULT NULL,
  mime_type varchar(255) DEFAULT NULL,
  file_size decimal(10, 0) DEFAULT NULL,
  data longblob DEFAULT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB;


ALTER TABLE form_template ADD printed_doc int DEFAULT NULL;

ALTER TABLE form_template
ADD CONSTRAINT FK_form_template_printed_doc FOREIGN KEY (printed_doc)
REFERENCES printed_doc (id);