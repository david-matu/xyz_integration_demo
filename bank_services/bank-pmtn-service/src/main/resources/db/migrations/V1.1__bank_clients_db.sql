-- -----------------------------------------------------
-- Table `bank_clients`.`bank_clients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bank_clients`.`bank_clients` (
  `CLIENT_ID` VARCHAR(255) NOT NULL,
  `INSTITUTION_NAME` VARCHAR(255) NULL,
  `VALIDATION_ENDPOINT` VARCHAR(255) NULL,
  `PAYMENT_NOTIFICATION_EP` VARCHAR(45) NULL,
  PRIMARY KEY (`CLIENT_ID`));

-- -----------------------------------------------------
-- Table `bank_clients`.`payment_notifications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bank_clients`.`payment_notifications` (
  `NOTIFICATION_ID` VARCHAR(255) NOT NULL,
  `CLIENT_CALLBACK_URL` VARCHAR(255) NOT NULL,
  `CLIENT_ID` VARCHAR(255) NULL,
  `PAYMENT_BODY` JSON NULL,
  `QUEUED_AT` DATETIME NULL,
  `IS_SENT` INT(1) NULL,
  `SENT_AT` DATETIME NULL,
  `IS_ACKNOWLEDGED` INT(1) NULL,
  `ACKNOWLEDGEMENT_RESPONSE` TEXT NULL,
  `SEND_COUNT` INT(2) NULL COMMENT 'We do not event expect a single message can be sent more than thrice, so a memory of upto 99 characters is fine',
  `COMMENT_LOG` TEXT NULL,
  PRIMARY KEY (`NOTIFICATION_ID`));
