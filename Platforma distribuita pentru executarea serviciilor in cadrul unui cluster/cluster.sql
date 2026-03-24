-- cluster.code_message definition

CREATE TABLE `code_message` (
  `id` varchar(255) NOT NULL,
  `code_record` varchar(100) NOT NULL,
  `content_bytes` mediumblob DEFAULT NULL,
  `content_string` text NOT NULL,
  `file_name` varchar(200) NOT NULL,
  `is_code_class` bit(1) DEFAULT NULL,
  `is_start_up` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_code_record` (`code_record`),
  CONSTRAINT `fk_code_record` FOREIGN KEY (`code_record`) REFERENCES `code_record` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- cluster.code_record definition

CREATE TABLE `code_record` (
  `id` varchar(255) NOT NULL,
  `description` varchar(5000) NOT NULL,
  `is_public` varchar(255) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `type_run` int(11) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_id` (`user_id`),
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- cluster.feedback_dto definition

CREATE TABLE `feedback_dto` (
  `id` varchar(255) NOT NULL,
  `content` varchar(5000) NOT NULL,
  `email` varchar(200) NOT NULL,
  `subject` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- cluster.ip_public_address definition

CREATE TABLE `ip_public_address` (
  `id` varchar(255) NOT NULL,
  `date_time` datetime(6) DEFAULT NULL,
  `ip` varchar(100) NOT NULL,
  `port` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_IP_PORT` (`ip`,`port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- cluster.`user` definition

CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `description` varchar(500) NOT NULL,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `language` varchar(7) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` tinyint(4) NOT NULL CHECK (`role` between 0 and 1),
  `username` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_USERNAME` (`username`),
  UNIQUE KEY `UK_EMAIL` (`email`),
  UNIQUE KEY `UK_e9iv4w4i25ghqs7isktgln9gx` (`description`),
  UNIQUE KEY `UK_n09rrji29tedrsrsmxkh9a2ec` (`first_name`),
  UNIQUE KEY `UK_qct51p6rq4j43jokn6wp42e5b` (`last_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE USER 'userCRUD'@'%' IDENTIFIED BY 'password';
CREATE USER 'adminCRUD'@'%' IDENTIFIED BY 'password';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.user TO 'userCRUD'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.code_message TO 'userCRUD'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.code_record TO 'userCRUD'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.ip_public_address TO 'adminCRUD'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON cluster.ip_public_address TO 'userCRUD'@'%';