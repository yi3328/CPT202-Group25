CREATE DATABASE  IF NOT EXISTS `specialist_hub` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `specialist_hub`;
-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: specialist_hub
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `appointmentid` int NOT NULL AUTO_INCREMENT,
  `customerid` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `specialistid` int DEFAULT NULL,
  `time_slot` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`appointmentid`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customerid` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(255) DEFAULT NULL,
  `customer_number` varchar(255) NOT NULL,
  `customer_password` varchar(255) NOT NULL,
  PRIMARY KEY (`customerid`),
  UNIQUE KEY `UK_t74y58jagthxqxysuw9l0jx6y` (`customer_number`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'jolin','u001','050301'),(2,'Alice Smith','CUST001','123456'),(3,'Bob Johnson','CUST002','123456'),(4,'Charlie Brown','CUST003','123456'),(5,'Diana Prince','CUST004','123456'),(6,'Evan Wright','CUST005','123456'),(7,'Fiona Gallagher','CUST006','123456'),(8,'George Martin','CUST007','123456'),(9,'Hannah Abbott','CUST008','123456'),(10,'Ian Somerhalder','CUST009','123456'),(11,'Julia Roberts','CUST010','123456'),(12,'Kevin Hart','CUST011','123456'),(13,'Luna Lovegood','CUST012','123456'),(14,'Mason Mount','CUST013','123456'),(15,'Nina Dobrev','CUST014','123456'),(16,'Oliver Twist','CUST015','123456'),(17,'Penelope Cruz','CUST016','123456'),(18,'Quinn Fabray','CUST017','123456'),(19,'Rachel Green','CUST018','123456'),(20,'Steve Rogers','CUST019','123456'),(21,'Tony Stark','CUST020','123456');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `orderid` int NOT NULL AUTO_INCREMENT,
  `customerid` int DEFAULT NULL,
  `order_status` int DEFAULT NULL,
  `specialistid` int DEFAULT NULL,
  PRIMARY KEY (`orderid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,101,2,1002),(2,101,2,1001),(3,1,2,1001),(4,1,2,1001),(5,1,2,1002),(6,1,2,1002);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `paymentid` int NOT NULL AUTO_INCREMENT,
  `orderorderid` int DEFAULT NULL,
  `orderid` int DEFAULT NULL,
  `payment_amount` decimal(38,2) DEFAULT NULL,
  `payment_method` int DEFAULT NULL,
  `payment_status` int DEFAULT NULL,
  PRIMARY KEY (`paymentid`),
  UNIQUE KEY `UK_qfkywdiynl787pptqqpje26qe` (`orderid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,1,1,150.00,1,1),(2,4,4,150.00,1,1),(3,5,5,150.00,1,1),(4,6,6,150.00,1,1);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specialist_availability`
--

DROP TABLE IF EXISTS `specialist_availability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialist_availability` (
  `availabilityid` int NOT NULL AUTO_INCREMENT,
  `available_date` date DEFAULT NULL,
  `is_booked` int DEFAULT NULL,
  `specialistid` int DEFAULT NULL,
  `specialist_timeslot` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`availabilityid`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specialist_availability`
--

LOCK TABLES `specialist_availability` WRITE;
/*!40000 ALTER TABLE `specialist_availability` DISABLE KEYS */;
INSERT INTO `specialist_availability` VALUES (1,'2026-04-20',0,1001,'09:00 - 10:00'),(2,'2026-04-21',0,1002,'14:00 - 15:00'),(3,'2026-04-21',0,1001,'19:00 - 20:00'),(4,'2026-05-10',0,1001,'09:00 - 10:00'),(5,'2026-05-10',0,1001,'14:00 - 15:00'),(6,'2026-05-11',0,1001,'10:30 - 11:30'),(7,'2026-05-11',0,1001,'16:00 - 17:00'),(8,'2026-05-12',0,1001,'19:00 - 20:00'),(9,'2026-05-10',0,1002,'10:30 - 11:30'),(10,'2026-05-10',0,1002,'16:00 - 17:00'),(11,'2026-05-12',0,1002,'09:00 - 10:00'),(12,'2026-05-13',0,1002,'14:00 - 15:00'),(13,'2026-05-14',0,1002,'19:00 - 20:00'),(14,'2026-05-11',0,1003,'09:00 - 10:00'),(15,'2026-05-11',0,1003,'14:00 - 15:00'),(16,'2026-05-13',0,1003,'10:30 - 11:30'),(17,'2026-05-14',0,1003,'16:00 - 17:00'),(18,'2026-05-15',0,1003,'09:00 - 10:00'),(19,'2026-05-10',0,1004,'09:00 - 10:00'),(20,'2026-05-12',0,1004,'14:00 - 15:00'),(21,'2026-05-14',0,1004,'10:30 - 11:30'),(22,'2026-05-15',0,1004,'16:00 - 17:00'),(23,'2026-05-15',0,1004,'19:00 - 20:00'),(24,'2026-05-11',0,1005,'10:30 - 11:30'),(25,'2026-05-12',0,1005,'16:00 - 17:00'),(26,'2026-05-13',0,1005,'09:00 - 10:00'),(27,'2026-05-13',0,1005,'19:00 - 20:00'),(28,'2026-05-14',0,1005,'14:00 - 15:00');
/*!40000 ALTER TABLE `specialist_availability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specialists`
--

DROP TABLE IF EXISTS `specialists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialists` (
  `specialistid` int NOT NULL AUTO_INCREMENT,
  `specialist_expertise` varchar(255) DEFAULT NULL,
  `specialist_name` varchar(255) DEFAULT NULL,
  `specialist_number` varchar(255) DEFAULT NULL,
  `specialist_password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`specialistid`)
) ENGINE=InnoDB AUTO_INCREMENT=1006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specialists`
--

LOCK TABLES `specialists` WRITE;
/*!40000 ALTER TABLE `specialists` DISABLE KEYS */;
INSERT INTO `specialists` VALUES (1001,'IT & Digital','Alexander Chen','SPEC001','123456'),(1002,'Legal & Risk','Sophia Miller','SPEC002','123456'),(1003,'Finance & Strategy','James Wilson','SPEC003','123456'),(1004,'Medical Consultation','Emily Davis','SPEC004','123456'),(1005,'Engineering Design','Michael Brown','SPEC005','123456');
/*!40000 ALTER TABLE `specialists` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-14 14:12:06
