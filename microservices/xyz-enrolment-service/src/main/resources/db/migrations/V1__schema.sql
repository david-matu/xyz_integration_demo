-- -----------------------------------------------------
-- Schema xyz_unversity
-- -----------------------------------------------------
-- This is a minimal schema that will facilitate Student Validation and Payment notification operations. That essentially will translate to two tables: ENROLMENT and PAYMENTS.
-- The Institution Database is quite complex and would invlove a lot of tables to complete the logic that includes curriculum data around a Student. Therefore, the aforementioned tables will serve as touchpoints.
-- 
-- The ENROLMENT table would essentially be a view of more than one tables (not included here) like STUDENT_REGISTRATIONS.
-- The PAYMENT table would easily correlate with an INVOICE table which gets data from generated info, let's say GRADINGS, PROMOTIONS

-- -----------------------------------------------------
-- Schema xyz_unversity
--
-- This is a minimal schema that will facilitate Student Validation and Payment notification operations. That essentially will translate to two tables: ENROLMENT and PAYMENTS.
-- The Institution Database is quite complex and would invlove a lot of tables to complete the logic that includes curriculum data around a Student. Therefore, the aforementioned tables will serve as touchpoints.
-- 
-- The ENROLMENT table would essentially be a view of more than one tables (not included here) like STUDENT_REGISTRATIONS.
-- The PAYMENT table would easily correlate with an INVOICE table which gets data from generated info, let's say GRADINGS, PROMOTIONS
-- -----------------------------------------------------
-- CREATE SCHEMA IF NOT EXISTS `xyz_unversity` ;
-- USE `xyz_unversity` ;
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table `enrolment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `enrolment` (
  `STUDENT_ID` VARCHAR(200) NOT NULL,
  `FIRST_NAME` VARCHAR(25) NULL,
  `LAST_NAME` VARCHAR(45) NULL,
  `ACCOUNT_NUMBER` VARCHAR(200) NULL,
  `STATUS` VARCHAR(45) NULL,
  PRIMARY KEY (`STUDENT_ID`))
ENGINE = InnoDB
COMMENT = 'Store records of active students. Students for which payments can be made.';


-- -----------------------------------------------------
-- Table `payments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
  `PAYMENT_ID` VARCHAR(255) NOT NULL,
  `EXTERNAL_REFERENCE` VARCHAR(255) NULL COMMENT 'A reference of the message as issued from external system',
  `FOR_INVOICE_ID` VARCHAR(50) NULL,
  `STUDENT_ID` VARCHAR(50) NULL,
  `AMOUNT_PAID` DOUBLE NULL,
  `DATE_PAID` DATETIME NULL,
  `WALLET` VARCHAR(50) NULL COMMENT 'The XYZ account that the money resides e.g. Family Bank account, Bursary account etc',
  `COMMENT` TEXT NULL,
  PRIMARY KEY (`PAYMENT_ID`))
COMMENT = 'Store records for received payments, essentially from Bank';