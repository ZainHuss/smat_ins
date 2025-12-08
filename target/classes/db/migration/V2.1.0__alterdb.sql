alter table task add is_active bit(1) DEFAULT NULL;

alter table company add contact_person varchar(255) DEFAULT NULL;

alter table company add details varchar(3000) DEFAULT NULL;
