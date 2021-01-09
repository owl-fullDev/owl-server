CREATE DATABASE  IF NOT EXISTS `OWL_Database` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `OWL_Database`;
-- MySQL dump 10.13  Distrib 8.0.22, for macos10.15 (x86_64)
--
-- Host: localhost    Database: OWL_Database
-- ------------------------------------------------------
-- Server version	8.0.22

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
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `left_eye_add` double NOT NULL,
  `left_eye_axis` int NOT NULL,
  `left_eye_cylinder` double NOT NULL,
  `left_eye_prism` varchar(255) NOT NULL,
  `left_eye_sphere` double NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `pupil_distance` int NOT NULL,
  `right_eye_add` double NOT NULL,
  `right_eye_axis` int NOT NULL,
  `right_eye_cylinder` double NOT NULL,
  `right_eye_prism` varchar(255) NOT NULL,
  `right_eye_sphere` double NOT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'1','1','1',1,1,1,'1',1,'1',1,1,1,1,'1',1),(2,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(3,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(4,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(5,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(6,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(7,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(8,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(9,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(10,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(11,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(12,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75),(13,'mario@owl.com','Test','1',2,240,-0.25,'-',1.75,'08119001785',20,2,180,-0.25,'-',1.75);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `employee_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `job_title` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `store_id` int DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  KEY `FKeg6451w5jta9oobtdgfe5c3n5` (`store_id`),
  CONSTRAINT `FKeg6451w5jta9oobtdgfe5c3n5` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'1','1','1','1','1',1),(2,'2','2','2','2','2',2),(3,'11','11','11','11','11',1),(4,'a','a','a','a','1',NULL);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` varchar(255) NOT NULL,
  `image_link` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_price` double NOT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES ('1',NULL,'1',100),('11',NULL,'11',1111),('2',NULL,'2',200),('22',NULL,'22222',2222),('3',NULL,'33333',33),('333',NULL,'333',333);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion`
--

DROP TABLE IF EXISTS `promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotion` (
  `promotion_id` int NOT NULL AUTO_INCREMENT,
  `percentage` int DEFAULT NULL,
  `promotion_name` varchar(255) NOT NULL,
  PRIMARY KEY (`promotion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion`
--

LOCK TABLES `promotion` WRITE;
/*!40000 ALTER TABLE `promotion` DISABLE KEYS */;
INSERT INTO `promotion` VALUES (1,1,'1'),(2,2,'2');
/*!40000 ALTER TABLE `promotion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale`
--

DROP TABLE IF EXISTS `sale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale` (
  `sale_id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int NOT NULL,
  `final_deposit_amount` double DEFAULT NULL,
  `final_deposit_date` varchar(255) DEFAULT NULL,
  `final_deposit_type` varchar(255) DEFAULT NULL,
  `grand_total` double NOT NULL,
  `initial_deposit_amount` double NOT NULL,
  `initial_deposit_date` varchar(255) NOT NULL,
  `initial_deposit_type` varchar(255) NOT NULL,
  `pickup_date` varchar(255) DEFAULT NULL,
  `promotion_id` int NOT NULL,
  `store_id` int NOT NULL,
  `customer_id` int NOT NULL,
  PRIMARY KEY (`sale_id`),
  KEY `FKjw88ojfoqquyd9f1obip1ar0g` (`customer_id`),
  CONSTRAINT `FKjw88ojfoqquyd9f1obip1ar0g` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale`
--

LOCK TABLES `sale` WRITE;
/*!40000 ALTER TABLE `sale` DISABLE KEYS */;
INSERT INTO `sale` VALUES (1,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,3),(2,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,4),(3,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,5),(4,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,6),(5,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,7),(6,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,8),(7,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,9),(8,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,10),(9,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,11),(10,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,12),(11,1,0,NULL,NULL,28282828.111,111.11,'2020-12-22T11:11:11','Debit: BCA',NULL,2,1,13);
/*!40000 ALTER TABLE `sale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale_detail`
--

DROP TABLE IF EXISTS `sale_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale_detail` (
  `sale_detail_id` int NOT NULL AUTO_INCREMENT,
  `product_id` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `sale_id` int NOT NULL,
  PRIMARY KEY (`sale_detail_id`),
  KEY `FKgnpg9v1mvi1nyhc18vdcyuh98` (`sale_id`),
  CONSTRAINT `FKgnpg9v1mvi1nyhc18vdcyuh98` FOREIGN KEY (`sale_id`) REFERENCES `sale` (`sale_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale_detail`
--

LOCK TABLES `sale_detail` WRITE;
/*!40000 ALTER TABLE `sale_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `sale_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store` (
  `store_id` int NOT NULL AUTO_INCREMENT,
  `location` varchar(255) NOT NULL,
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES (1,'1'),(2,'2'),(3,'3');
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_promotion`
--

DROP TABLE IF EXISTS `store_promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_promotion` (
  `store_id` int NOT NULL,
  `promotion_id` int NOT NULL,
  KEY `FK9k4d5lea9fkn3sjudvlcpa5bk` (`promotion_id`),
  KEY `FKsdpsvcnf4jdcyrs9t88xy157j` (`store_id`),
  CONSTRAINT `FK9k4d5lea9fkn3sjudvlcpa5bk` FOREIGN KEY (`promotion_id`) REFERENCES `promotion` (`promotion_id`),
  CONSTRAINT `FKsdpsvcnf4jdcyrs9t88xy157j` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_promotion`
--

LOCK TABLES `store_promotion` WRITE;
/*!40000 ALTER TABLE `store_promotion` DISABLE KEYS */;
INSERT INTO `store_promotion` VALUES (1,1),(1,2),(1,1),(1,2),(2,1),(2,1),(2,1),(2,1),(3,1),(3,1),(3,1),(3,1);
/*!40000 ALTER TABLE `store_promotion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_quantity`
--

DROP TABLE IF EXISTS `store_quantity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_quantity` (
  `store_quantity_id` int NOT NULL AUTO_INCREMENT,
  `instore_quantity` int DEFAULT NULL,
  `product_id` varchar(255) NOT NULL,
  `store_id` int NOT NULL,
  PRIMARY KEY (`store_quantity_id`),
  KEY `FK12k7koos7nw6jm9o4jkf4e0b8` (`store_id`),
  CONSTRAINT `FK12k7koos7nw6jm9o4jkf4e0b8` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_quantity`
--

LOCK TABLES `store_quantity` WRITE;
/*!40000 ALTER TABLE `store_quantity` DISABLE KEYS */;
INSERT INTO `store_quantity` VALUES (1,11,'1',1),(2,1111,'11',1),(3,22,'2',1),(4,22222,'22',1),(5,3,'3',1),(6,33333,'333',1),(7,11,'1',2),(8,1111,'11',2),(9,22,'2',2),(10,22222,'22',2),(11,3,'3',2),(12,33333,'333',2),(13,11,'1',3),(14,1111,'11',3),(15,22,'2',3),(16,22222,'22',3),(17,3,'3',3),(18,33333,'333',3);
/*!40000 ALTER TABLE `store_quantity` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-01-08 14:07:18
