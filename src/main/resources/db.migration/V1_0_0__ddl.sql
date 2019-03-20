CREATE TABLE `regions` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8mb4_general_ci;

CREATE TABLE `programs` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `theme` VARCHAR(255) NOT NULL,
  `region_id` INT NOT NULL,
  `region_details` VARCHAR(255),
  `intro` VARCHAR(1000),
  `details` VARCHAR(65535)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8mb4_general_ci;
