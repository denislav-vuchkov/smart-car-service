-- --------------------------------------------------------
-- Host:                         eporqep6b4b8ql12.chr7pe7iynqr.eu-west-1.rds.amazonaws.com
-- Server version:               10.4.21-MariaDB-log - Source distribution
-- Server OS:                    Linux
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for vbehq9xh6ueil92g
CREATE DATABASE IF NOT EXISTS `vbehq9xh6ueil92g` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `vbehq9xh6ueil92g`;

-- Dumping structure for table vbehq9xh6ueil92g.history_of_services
CREATE TABLE IF NOT EXISTS `history_of_services` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `visit_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  `service_name` text COLLATE utf8mb4_bin NOT NULL,
  `service_price_bgn` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `visits_history_id_uindex` (`id`),
  KEY `visits_history_visits_id_fk` (`visit_id`),
  CONSTRAINT `visits_history_visits_id_fk` FOREIGN KEY (`visit_id`) REFERENCES `visits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.payments
CREATE TABLE IF NOT EXISTS `payments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `visit_id` int(11) NOT NULL,
  `payment_method_id` int(11) NOT NULL,
  `unique_payment_id` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payments_id_uindex` (`id`),
  UNIQUE KEY `payments_payment_id_uindex` (`unique_payment_id`) USING HASH,
  KEY `payments_payment_methods_id_fk` (`payment_method_id`),
  KEY `payments_visits_id_fk` (`visit_id`),
  CONSTRAINT `payments_payment_methods_id_fk` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_methods` (`id`),
  CONSTRAINT `payments_visits_id_fk` FOREIGN KEY (`visit_id`) REFERENCES `visits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.payment_methods
CREATE TABLE IF NOT EXISTS `payment_methods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_methods_id_uindex` (`id`),
  UNIQUE KEY `payment_methods_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.reset_password_tokens
CREATE TABLE IF NOT EXISTS `reset_password_tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token` text CHARACTER SET latin1 NOT NULL,
  `created_at` datetime NOT NULL,
  `expired_at` datetime NOT NULL,
  `accessed` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `confirmation_tokens_id_uindex` (`id`),
  KEY `confirmation_tokens_users_fk` (`user_id`),
  CONSTRAINT `confirmation_tokens_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` enum('CUSTOMER','EMPLOYEE') COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roles_id_uindex` (`id`),
  UNIQUE KEY `roles_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.services
CREATE TABLE IF NOT EXISTS `services` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8mb4_bin NOT NULL,
  `price_bgn` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_id_uindex` (`id`),
  UNIQUE KEY `service_name_uindex` (`name`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `password` text COLLATE utf8mb4_bin NOT NULL,
  `email` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `phone_number` varchar(10) COLLATE utf8mb4_bin NOT NULL,
  `role_id` int(11) NOT NULL DEFAULT 3,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0,
  `registered_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_id_uindex` (`id`),
  UNIQUE KEY `users_email_uindex` (`email`),
  UNIQUE KEY `users_phone_number_uindex` (`phone_number`),
  KEY `users_roles_id_fk` (`role_id`),
  CONSTRAINT `users_roles_id_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.vehicles
CREATE TABLE IF NOT EXISTS `vehicles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `license_plate` varchar(8) COLLATE utf8mb4_bin NOT NULL,
  `vehicle_identification_number` varchar(17) COLLATE utf8mb4_bin NOT NULL,
  `year_of_creation` int(4) NOT NULL,
  `model_id` int(11) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vehicle_id_uindex` (`id`),
  UNIQUE KEY `vehicle_vehicle_identification_number_uindex` (`vehicle_identification_number`),
  UNIQUE KEY `vehicles_license_plate_uindex` (`license_plate`),
  KEY `vehicle_vehicle_models_fk` (`model_id`),
  KEY `vehicles_users_fk` (`user_id`),
  CONSTRAINT `vehicle_vehicle_models_fk` FOREIGN KEY (`model_id`) REFERENCES `vehicle_models` (`id`),
  CONSTRAINT `vehicles_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.vehicle_makes
CREATE TABLE IF NOT EXISTS `vehicle_makes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vehicle_brands_id_uindex` (`id`),
  UNIQUE KEY `vehicle_brands_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.vehicle_models
CREATE TABLE IF NOT EXISTS `vehicle_models` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `make_id` int(11) NOT NULL,
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vehicle_models_id_uindex` (`id`),
  UNIQUE KEY `vehicle_models_name_uindex` (`name`),
  KEY `vehicle_models_vehicle_brands_fk` (`make_id`),
  CONSTRAINT `vehicle_models_vehicle_brands_fk` FOREIGN KEY (`make_id`) REFERENCES `vehicle_makes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.visits
CREATE TABLE IF NOT EXISTS `visits` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `status_id` int(11) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `visits_id_uindex` (`id`),
  KEY `visits_users_id_fk` (`user_id`),
  KEY `visits_vehicles_fk` (`vehicle_id`),
  KEY `visits_visit_status_id_fk` (`status_id`),
  CONSTRAINT `visits_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `visits_vehicles_fk` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`),
  CONSTRAINT `visits_visit_status_id_fk` FOREIGN KEY (`status_id`) REFERENCES `visit_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.visit_pictures
CREATE TABLE IF NOT EXISTS `visit_pictures` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `visit_id` int(11) NOT NULL,
  `photo` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `token` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `visit_pictures_id_uindex` (`id`),
  KEY `visit_pictures_visits_id_fk` (`visit_id`),
  CONSTRAINT `visit_pictures_visits_id_fk` FOREIGN KEY (`visit_id`) REFERENCES `visits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

-- Dumping structure for table vbehq9xh6ueil92g.visit_status
CREATE TABLE IF NOT EXISTS `visit_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `visit_status_id_uindex` (`id`),
  UNIQUE KEY `visit_status_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
