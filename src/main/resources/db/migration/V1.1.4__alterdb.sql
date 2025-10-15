alter table cabinet_location drop location;
ALTER TABLE cabinet_location ADD win_location varchar(300) NULL;
ALTER TABLE cabinet_location ADD linux_location varchar(300) NULL;
ALTER TABLE cabinet_location ADD mac_location varchar(300) NULL;

INSERT INTO cabinet_location(id, win_location,linux_location,mac_location, is_default, created_date, sys_user) VALUES
(1, 'c:\\MyCabinets','/opt/MyCabinets','/MyCabinets', TRUE, NULL, NULL)
ON DUPLICATE KEY UPDATE id=VALUES(id), win_location=VALUES(win_location),linux_location=VALUES(linux_location) , mac_location=VALUES(mac_location) , is_default=VALUES(is_default), created_date=VALUES(created_date), sys_user=VALUES(sys_user);