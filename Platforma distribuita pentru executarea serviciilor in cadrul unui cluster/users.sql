drop user 'userCRUD'@'localhost';

CREATE USER 'userCRUD'@'%' IDENTIFIED BY 'password';

drop user 'adminCRUD'@'localhost';

CREATE USER 'adminCRUD'@'%' IDENTIFIED BY 'password';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.user TO 'userCRUD'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.code_message TO 'userCRUD'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.code_record TO 'userCRUD'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.ip_public_address TO 'adminCRUD'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.log_messages_execution_node TO 'userCRUD'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.log_messages_http_node TO 'userCRUD'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.log_messages_master_node TO 'userCRUD'@'%';

FLUSH PRIVILEGES;
