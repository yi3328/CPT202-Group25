-- Admin table
CREATE TABLE IF NOT EXISTS `admins` (
  `admin_id` int NOT NULL AUTO_INCREMENT,
  `admin_number` varchar(255) NOT NULL,
  `admin_password` varchar(255) NOT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Add is_active to customers table
ALTER TABLE `customers` ADD COLUMN `is_active` int DEFAULT 1;

-- Add is_active to specialists table
ALTER TABLE `specialists` ADD COLUMN `is_active` int DEFAULT 1;

-- Add room_number to specialists table
ALTER TABLE `specialists` ADD COLUMN `room_number` varchar(50) DEFAULT 'Room 101';

-- Insert default admin (admin001 / 123456)
INSERT INTO `admins` (`admin_number`, `admin_password`) VALUES ('admin001', '123456');
