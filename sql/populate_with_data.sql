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

-- Dumping data for table vbehq9xh6ueil92g.history_of_services: ~28 rows (approximately)
/*!40000 ALTER TABLE `history_of_services` DISABLE KEYS */;
INSERT INTO `history_of_services` (`id`, `visit_id`, `service_id`, `service_name`, `service_price_bgn`) VALUES
	(11, 40, 8, 'Belts & Hoses', 376),
	(12, 41, 4, 'Alternators & Starters', 165),
	(13, 41, 8, 'Belts & Hoses', 376),
	(14, 42, 7, 'Batteries & Alternator', 334),
	(15, 44, 4, 'Alternators & Starters', 165),
	(16, 44, 8, 'Belts & Hoses', 376),
	(17, 45, 9, 'Brake Repair & Service', 429),
	(18, 45, 4, 'Alternators & Starters', 165),
	(19, 46, 10, 'Car Inspection', 160),
	(20, 46, 6, 'Axle Repair', 331),
	(21, 47, 10, 'Car Inspection', 160),
	(22, 47, 6, 'Axle Repair', 331),
	(23, 47, 11, 'Catalytic Converters', 291),
	(28, 49, 37, 'Muffler Repair/Exhaust', 409),
	(29, 50, 8, 'Belts & Hoses', 376),
	(30, 51, 9, 'Brake Repair & Service', 429),
	(31, 52, 3, 'Air Filter/Fuel Filter', 461),
	(32, 52, 8, 'Belts & Hoses', 376),
	(41, 39, 47, 'Starting System', 407),
	(42, 39, 8, 'Belts & Hoses', 376),
	(43, 57, 60, 'Windshield Wipers', 123),
	(44, 57, 58, 'Wheel Balancing', 401),
	(53, 62, 5, 'AntiFreeze', 323),
	(54, 63, 5, 'AntiFreeze', 323),
	(63, 38, 6, 'Axle Repair', 331),
	(82, 76, 6, 'Axle Repair', 331),
	(83, 76, 50, 'Timing Belt Service', 198),
	(84, 77, 9, 'Brake Repair & Service', 429),
	(85, 77, 7, 'Batteries & Alternator', 334),
	(86, 78, 3, 'Air Filter/Fuel Filter', 461),
	(87, 78, 6, 'Axle Repair', 331),
	(88, 78, 40, 'Power Steering System', 428),
	(89, 78, 5, 'AntiFreeze', 323),
	(90, 79, 8, 'Belts & Hoses', 376),
	(91, 79, 6, 'Axle Repair', 331),
	(92, 80, 6, 'Axle Repair', 331),
	(93, 80, 8, 'Belts & Hoses', 376);
/*!40000 ALTER TABLE `history_of_services` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.payments: ~0 rows (approximately)
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.payment_methods: ~2 rows (approximately)
/*!40000 ALTER TABLE `payment_methods` DISABLE KEYS */;
INSERT INTO `payment_methods` (`id`, `name`) VALUES
	(1, 'PAYPAL'),
	(2, 'STRIPE');
/*!40000 ALTER TABLE `payment_methods` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.reset_password_tokens: ~21 rows (approximately)
/*!40000 ALTER TABLE `reset_password_tokens` DISABLE KEYS */;
INSERT INTO `reset_password_tokens` (`id`, `user_id`, `token`, `created_at`, `expired_at`, `accessed`) VALUES
	(10, 1, '2976ae38-9d34-4883-be42-cd3047c2f38f', '2022-04-01 17:07:04', '2022-04-01 18:07:04', 1),
	(11, 1, '815d021c-029b-4d6e-bf80-484cbfe8a3de', '2022-04-01 17:51:11', '2022-04-01 18:51:11', 1),
	(12, 15, '43107714-2a08-41c2-9275-785f899b8cb5', '2022-04-02 12:02:18', '2022-04-02 13:02:18', 1),
	(13, 1, '227e43cf-913a-4a63-949d-c5cba83b9280', '2022-04-02 21:59:56', '2022-04-02 22:59:56', 1),
	(14, 1, '54c21e4f-2a2a-493b-a289-14f6af2551cd', '2022-04-03 12:54:53', '2022-04-03 13:54:53', 1),
	(15, 1, 'a882c557-4fa9-4deb-aac3-c6e2aa466904', '2022-04-05 10:26:35', '2022-04-05 11:26:35', 0),
	(16, 2, '3170ac0c-eceb-4a26-a414-c7584383b688', '2022-04-06 10:42:44', '2022-04-06 11:42:44', 1),
	(17, 2, '6a62c8fb-d459-4d92-ad06-f47ccb1b51cc', '2022-04-06 16:52:31', '2022-04-06 17:52:31', 1),
	(18, 2, 'f27916fa-7313-483e-9dc8-dc95090c28db', '2022-04-06 17:28:49', '2022-04-06 18:28:49', 1),
	(19, 1, '19541739-f5a3-49d0-8dcd-0693771a94b9', '2022-04-07 13:39:51', '2022-04-07 14:39:51', 0),
	(20, 1, '16706d3f-3195-4233-94b3-31c1b0291802', '2022-04-08 12:41:38', '2022-04-08 13:41:38', 0),
	(21, 1, '2196bfcd-dc7e-4bca-a20c-8008ce9ca7b2', '2022-04-08 12:45:56', '2022-04-08 13:45:56', 1),
	(22, 1, '88b5f27e-c82b-41f6-8bb2-aa55487ba92a', '2022-04-10 20:30:50', '2022-04-10 21:30:50', 1),
	(23, 3, '6738bee1-fe30-4bc4-863e-55a4479d21ad', '2022-04-14 14:16:24', '2022-04-14 15:16:24', 0),
	(24, 1, '5935a9ac-3467-45d1-9a52-b245f1b61a2f', '2022-04-14 14:17:25', '2022-04-14 15:17:25', 0),
	(25, 1, '6eb99539-a7b8-417c-9d0b-d4f41bf24a6f', '2022-04-19 11:52:51', '2022-04-19 12:52:51', 1),
	(26, 2, '9ab71e0e-3a4c-4429-95b6-cca3dcbc0415', '2022-04-19 12:11:57', '2022-04-19 13:11:57', 1),
	(27, 1, 'db65c777-6bc4-4907-b4de-b76be01eca1f', '2022-04-19 09:12:45', '2022-04-19 10:12:45', 0),
	(28, 1, '75506e0b-fb6e-430c-98ce-e2b164e52399', '2022-04-19 12:15:19', '2022-04-19 13:15:19', 0),
	(29, 1, '8ecfd68d-422f-432c-a343-fe72892d1ea4', '2022-04-19 11:51:58', '2022-04-19 12:51:58', 1),
	(31, 1, '2c4d840e-e082-4a10-a868-95cd24b610ed', '2022-04-20 10:22:59', '2022-04-20 11:22:59', 0);
/*!40000 ALTER TABLE `reset_password_tokens` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.roles: ~2 rows (approximately)
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` (`id`, `name`) VALUES
	(3, 'CUSTOMER'),
	(2, 'EMPLOYEE');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.services: ~60 rows (approximately)
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` (`id`, `name`, `price_bgn`) VALUES
	(1, 'ABS Repairs', 282),
	(2, 'Air Conditioning Systems', 320),
	(3, 'Air Filter/Fuel Filter', 461),
	(4, 'Alternators & Starters', 165),
	(5, 'AntiFreeze', 323),
	(6, 'Axle Repair', 331),
	(7, 'Batteries & Alternator', 334),
	(8, 'Belts & Hoses', 376),
	(9, 'Brake Repair & Service', 429),
	(10, 'Car Inspection', 160),
	(11, 'Catalytic Converters', 291),
	(12, 'Check Engine Lights', 330),
	(13, 'Chrome Accessories', 438),
	(14, 'Cold Air Intakes', 103),
	(15, 'Complete Engine Repair', 393),
	(16, 'Computerized Engine Diagnostics', 308),
	(17, 'Cooling System Repair & Service', 248),
	(18, 'Custom & Classic Car', 402),
	(19, 'Custom Exhaust Systems', 185),
	(20, 'Custom Wheels', 207),
	(21, 'CV Shafts & Joints', 368),
	(22, 'Diesel Engine Repair', 220),
	(23, 'Diesel Fuel Injectors', 104),
	(24, 'Diesel Fuel Systems', 260),
	(25, 'Driveline & Differential', 227),
	(26, 'Electrical Systems', 386),
	(27, 'Emissions Repair', 440),
	(28, 'Engine Repair', 182),
	(29, 'Exhaust & Muffler Repair', 280),
	(30, 'Exterior & Accessories', 215),
	(31, 'Factory Scheduled Maintenance', 470),
	(32, 'Four-Wheel Drive Repair', 459),
	(33, 'Fuel Pumps & Lines', 141),
	(34, 'Fuel System Injector Cleaning', 214),
	(35, 'General Maintenance', 213),
	(36, 'Headlights & Headlamps', 396),
	(37, 'Muffler Repair/Exhaust', 409),
	(38, 'Oil Change, Filters & Lube', 477),
	(39, 'Performance Exhaust', 471),
	(40, 'Power Steering System', 428),
	(41, 'Power Window Repair', 268),
	(42, 'Rack & Pinion Steering Systems', 496),
	(43, 'Radiator /Cooling System', 428),
	(44, 'Serpentine Belt', 152),
	(45, 'Shocks and Struts', 225),
	(46, 'Springs & Suspension', 185),
	(47, 'Starting System', 407),
	(48, 'Steering & Chassis', 475),
	(49, 'Suspension', 341),
	(50, 'Timing Belt Service', 198),
	(51, 'Towing Service', 354),
	(52, 'Trailer Hitches', 407),
	(53, 'Transmission Service & Repair', 256),
	(54, 'Troubleshooting', 138),
	(55, 'Tune Up/ Fuel Economy', 365),
	(56, 'Universal Joint', 431),
	(57, 'Wheel Alignment', 125),
	(58, 'Wheel Balancing', 401),
	(59, 'Wheel Bearings', 421),
	(60, 'Windshield Wipers', 123);
/*!40000 ALTER TABLE `services` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.users: ~16 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone_number`, `role_id`, `is_deleted`, `registered_at`) VALUES
	(1, 'Tihomir', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'testing123tihomir@gmail.co', '0876051461', 3, 0, '2022-03-31 19:31:50'),
	(2, 'Denislav', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'dimitrovtihomir7@gmail.com', '0888654321', 2, 0, '2022-03-31 19:31:54'),
	(3, 'Dimitar', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'vy4kov@gmail.com', '0888010203', 3, 0, '2022-03-31 21:03:08'),
	(12, 'georgi', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'g.delchev93@gmail.com', '0876051462', 3, 0, '2022-04-01 10:01:46'),
	(13, 'Plamenna', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'pppavlova@gmail.com', '0876051463', 2, 0, '2022-04-01 17:57:28'),
	(14, 'tihomir.dimitrov', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'tihomir.dimitrov.1995@abv.bg', '0876051464', 2, 0, '2022-04-01 18:51:19'),
	(15, 'alexandra', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'sasha@gmail.com', '0888654322', 3, 0, '2022-04-02 12:01:05'),
	(19, 'tdimitrov', '$2a$10$UQYEn.YnvGh2NA5tMcY3BOP8cdWENYpTiNG28.oq4BYn77ZfWGbEe', 'random@gmail.com', '0876051411', 3, 0, '2022-04-03 11:02:52'),
	(20, 'victor', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'victor@gmail.com', '0876051422', 2, 0, '2022-04-03 11:07:12'),
	(32, 'alexandra2', '$2a$15$khY/sTCegaOZyQhxBkGTsuaXXSUlHv3MZtmQGYbt0Njc0XXvWgSo6', 'rand2om@gmail.com', '0876051423', 3, 0, '2022-04-10 16:02:38'),
	(33, 'Alexandra3', '$2a$15$Ig/5UNu92OdiFLpNIPBl1ei4.5W0xLPSC52eDfYoeSh7.GMr5oxjC', 'ra2ndom@gmail.com', '0876051434', 3, 0, '2022-04-10 16:18:09'),
	(34, 'georgi2', '$2a$10$0kUFeX9HPuAMHl67263NzO7sMQGKyAXbW.6eXHLCD69nj.iDpG.na', 'g3213.delchev93@gmail.com', '0876051467', 3, 0, '2022-04-10 20:34:07'),
	(35, 'alexandra23', '$2a$15$TZMClX1v/tXh8mBM.Uu5RuIHcMDNX8rA4AevD0HfXw7UTqP8H2.6K', 'g222.delchev93@gmail.com', '0876051432', 3, 0, '2022-04-10 21:15:17'),
	(36, 'georgi1', '$2a$15$Uwbr3Zov0/RorekrklyIYe/DOY5X3dLfjGLgQ.4hpneaizAG0nIZm', 'g1.delchev93@gmail.com', '0876051469', 3, 0, '2022-04-10 21:34:38'),
	(37, 'Alexandra31', '$2a$15$qMTDcKOXsqDY1Nbzth6cZus0pvi29izQTAbF5G6X891TYspVhydHO', 'ra2andom@gmail.com', '0876051421', 3, 1, '2022-04-10 21:59:46'),
	(38, 'Alexandra313', '$2a$15$tL0ejowyOwJa7UX/LbtZZeuGLzqPjXbWMZDe.5YAGKOe4zon.wrWq', 'ra2a3ndom@gmail.com', '0876051412', 3, 1, '2022-04-11 23:24:21');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.vehicles: ~31 rows (approximately)
/*!40000 ALTER TABLE `vehicles` DISABLE KEYS */;
INSERT INTO `vehicles` (`id`, `user_id`, `license_plate`, `vehicle_identification_number`, `year_of_creation`, `model_id`, `is_deleted`) VALUES
	(6, 1, 'CA6969TT', '1D7HA18D44J218509', 2001, 3, 0),
	(7, 1, 'BP7777OO', '4XAXH76A8AD092394', 2021, 4, 0),
	(8, 3, 'CA0000KK', '3VWSB81H8WM210368', 2009, 6, 0),
	(9, 3, 'CO1111PP', '1GYFK63878R248711', 2016, 7, 0),
	(10, 3, 'BP3333EE', '2B3HD46R02H210893', 2010, 1, 0),
	(12, 12, 'CO1234AA', '3B7KF23Z42M211215', 2016, 5, 0),
	(13, 3, 'CO7777OO', '3B3ES47C6WT211725', 2016, 5, 0),
	(14, 12, 'CO6786OP', 'VP1AB29P9A9LA4080', 2016, 7, 0),
	(15, 3, 'CA1212AA', 'WDDDJ72X97A116339', 2015, 8, 0),
	(16, 1, 'CO0011TT', '5NPDH4AE6BH023266', 2010, 6, 0),
	(17, 12, 'CO9991TT', '1HD1PDC392Y952267', 2011, 8, 0),
	(25, 3, 'TT6677PP', 'WP0CB298X4U661678', 2017, 9, 0),
	(26, 12, 'CA5675PT', '1FUJA6CV74DM34063', 2016, 10, 0),
	(28, 15, 'CO0967EE', 'JH4DA9450NS000826', 2009, 14, 0),
	(29, 15, 'CO0000TA', 'JH4DA9460PS008002', 2019, 10, 0),
	(30, 15, 'TT7777TT', 'JH4KA3240KC018604', 2019, 15, 0),
	(31, 12, 'TT6677TT', 'JH4DA1740JS012019', 2019, 16, 0),
	(32, 12, 'CA0099TT', 'JH4KA3260LC000123', 2017, 12, 0),
	(37, 32, 'CC1234XO', '1HGCA5645KA130342', 2017, 17, 0),
	(38, 33, 'CC1234AT', '1HGCA5645KA130343', 2009, 18, 0),
	(39, 15, 'CA7711OP', '1FMCU04112KA71263', 2014, 19, 0),
	(40, 15, 'CO4786OP', 'WDDDJ72X97A116331', 2015, 23, 0),
	(41, 37, 'CO1234AT', '3B7KF23Z42M211211', 2016, 27, 0),
	(42, 15, 'CO6784OT', 'VP1AB29P9A9LA4280', 2015, 25, 0),
	(43, 12, 'CO0000TT', 'JH4DA9460PS008102', 2015, 26, 0),
	(44, 15, 'DELETED', '1FTZR15VXXTB07251', 2017, 29, 1),
	(45, 38, 'CA0310KO', '1FTZR15VXXTB07252', 2019, 30, 1),
	(46, 34, 'CA4334OX', 'KM8NU12C070014858', 2012, 31, 1),
	(48, 35, 'CC4334PX', 'KM8NU12C070014768', 2018, 35, 0),
	(49, 15, 'OP5645HH', '2FDJF37G2BCA83848', 2017, 36, 0),
	(50, 12, 'CO8811OP', 'JH4DB1650NS000627', 2009, 15, 0);
/*!40000 ALTER TABLE `vehicles` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.vehicle_makes: ~24 rows (approximately)
/*!40000 ALTER TABLE `vehicle_makes` DISABLE KEYS */;
INSERT INTO `vehicle_makes` (`id`, `name`) VALUES
	(16, 'Bugatti'),
	(32, 'Dodge'),
	(6, 'Ferrari'),
	(15, 'Fiat'),
	(13, 'Ford'),
	(2, 'Honda'),
	(1, 'Hyundai'),
	(22, 'Kia'),
	(26, 'Lamborghini'),
	(34, 'Maserati'),
	(30, 'Mazda'),
	(4, 'McLaren'),
	(27, 'Mitsubishi'),
	(19, 'Nissan'),
	(37, 'Opel'),
	(29, 'Peugeot'),
	(25, 'Porsche'),
	(33, 'Renault'),
	(39, 'Seat'),
	(14, 'Skoda'),
	(31, 'Subaru'),
	(35, 'Suzuki'),
	(3, 'Toyota'),
	(36, 'Volkswagen');
/*!40000 ALTER TABLE `vehicle_makes` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.vehicle_models: ~32 rows (approximately)
/*!40000 ALTER TABLE `vehicle_models` DISABLE KEYS */;
INSERT INTO `vehicle_models` (`id`, `make_id`, `name`) VALUES
	(1, 1, 'i30'),
	(2, 2, 'Civic'),
	(3, 3, 'Supra'),
	(4, 4, 'P1'),
	(5, 1, 'Santa Fe'),
	(6, 2, 'CR-V'),
	(7, 3, 'Avensis'),
	(8, 3, 'Corolla'),
	(9, 1, 'i20'),
	(10, 6, 'Testarossa'),
	(12, 13, 'Focus'),
	(13, 14, 'Octavia'),
	(14, 1, 'i10'),
	(15, 15, '500'),
	(16, 16, 'Chiron'),
	(17, 13, 'Fiesta'),
	(18, 19, 'GT-R'),
	(19, 22, 'Sportage'),
	(20, 25, 'Macan'),
	(21, 26, 'Huracan'),
	(22, 27, 'Lancer'),
	(23, 29, '508'),
	(24, 30, 'MX-5'),
	(25, 31, 'Impreza'),
	(26, 32, 'Charger'),
	(27, 33, 'Megane'),
	(28, 34, 'GranTurismo'),
	(29, 35, 'Swift'),
	(30, 36, 'Passat'),
	(31, 37, 'Astra'),
	(34, 39, 'Ibiza'),
	(35, 37, 'Insignia'),
	(36, 33, 'Talisman');
/*!40000 ALTER TABLE `vehicle_models` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.visits: ~20 rows (approximately)
/*!40000 ALTER TABLE `visits` DISABLE KEYS */;
INSERT INTO `visits` (`id`, `user_id`, `vehicle_id`, `status_id`, `start_date`, `end_date`, `is_deleted`) VALUES
	(38, 1, 6, 5, '2022-01-07 00:00:00', NULL, 0),
	(39, 3, 8, 7, '2022-01-05 00:00:00', '2022-01-13 00:00:00', 0),
	(40, 1, 7, 5, '2022-01-16 00:00:00', NULL, 0),
	(41, 1, 16, 5, '2022-01-18 00:00:00', NULL, 0),
	(42, 15, 28, 7, '2022-02-10 00:00:00', '2022-02-15 00:00:00', 0),
	(44, 12, 14, 7, '2022-02-17 00:00:00', '2022-02-19 00:00:00', 0),
	(45, 3, 15, 7, '2022-02-18 00:00:00', '2022-02-20 00:00:00', 0),
	(46, 3, 15, 7, '2022-03-06 00:00:00', '2022-03-09 00:00:00', 0),
	(47, 3, 13, 6, '2022-03-07 00:00:00', '2022-03-11 00:00:00', 0),
	(49, 3, 15, 6, '2022-04-06 00:00:00', NULL, 0),
	(50, 3, 15, 6, '2022-04-06 00:00:00', NULL, 0),
	(51, 12, 26, 5, '2022-04-06 00:00:00', NULL, 0),
	(52, 15, 28, 5, '2022-04-06 00:00:00', NULL, 0),
	(57, 12, 14, 5, '2022-04-06 00:00:00', NULL, 0),
	(62, 32, 37, 4, '2022-04-10 00:00:00', NULL, 0),
	(63, 33, 38, 4, '2022-04-15 00:00:00', NULL, 0),
	(76, 3, 8, 6, '2022-04-19 00:00:00', NULL, 1),
	(77, 3, 13, 5, '2022-04-23 00:00:00', NULL, 0),
	(78, 3, 25, 4, '2022-04-20 00:00:00', NULL, 0),
	(79, 3, 25, 3, '2022-04-21 00:00:00', NULL, 0),
	(80, 3, 10, 3, '2022-04-27 00:00:00', NULL, 0);
/*!40000 ALTER TABLE `visits` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.visit_pictures: ~0 rows (approximately)
/*!40000 ALTER TABLE `visit_pictures` DISABLE KEYS */;
INSERT INTO `visit_pictures` (`id`, `visit_id`, `photo`, `token`) VALUES
	(11, 38, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1650392076/SmartGarage/VisitPhotos/visitID-38/token-23bbe9d2-e404-469a-96f1-cd85d03be417.jpg', '23bbe9d2-e404-469a-96f1-cd85d03be417'),
	(12, 38, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1650392176/SmartGarage/VisitPhotos/visitID-38/token-62bc64ed-1090-4cad-b6bb-ee7b1b0d6a16.jpg', '62bc64ed-1090-4cad-b6bb-ee7b1b0d6a16'),
	(13, 79, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1650449924/SmartGarage/VisitPhotos/visitID-79/token-fad11a00-bb3c-44f2-a906-c2baa5902d8d.jpg', 'fad11a00-bb3c-44f2-a906-c2baa5902d8d'),
	(14, 79, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1650451736/SmartGarage/VisitPhotos/visitID-79/token-d6843d3f-3443-4946-bfe7-e2f429f00ae3.jpg', 'd6843d3f-3443-4946-bfe7-e2f429f00ae3'),
	(15, 80, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1650539990/SmartGarage/VisitPhotos/visitID-80/token-298c5620-356f-435c-820b-9224ff1b61d7.jpg', '298c5620-356f-435c-820b-9224ff1b61d7');
/*!40000 ALTER TABLE `visit_pictures` ENABLE KEYS */;

-- Dumping data for table vbehq9xh6ueil92g.visit_status: ~6 rows (approximately)
/*!40000 ALTER TABLE `visit_status` DISABLE KEYS */;
INSERT INTO `visit_status` (`id`, `name`) VALUES
	(7, 'Completed'),
	(2, 'Declined'),
	(4, 'In Progress'),
	(3, 'Not Started'),
	(6, 'Ready (Settled)'),
	(5, 'Ready (Unpaid)'),
	(1, 'Requested');
/*!40000 ALTER TABLE `visit_status` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
