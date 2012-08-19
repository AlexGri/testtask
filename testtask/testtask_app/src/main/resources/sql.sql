delimiter $$

CREATE DATABASE `pd` /*!40100 DEFAULT CHARACTER SET utf8 */$$

delimiter $$

CREATE TABLE `position` (
  `id` int(11) NOT NULL,
  `position_name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `position_name_UNIQUE` (`position_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

delimiter $$

CREATE TABLE `employee` (
  `id` int(11) NOT NULL,
  `firstname` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `middlename` varchar(45) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `salary` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `position_idx` (`position`),
  CONSTRAINT `position_fk` FOREIGN KEY (`position`) REFERENCES `position` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

