package com.bank.services.pmtn.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentNotificationRepository extends CrudRepository<PaymentNotificationEntity, String> {
	
	@Transactional(readOnly = true)
	PaymentNotificationEntity findByPaymentRef(String notificationId);
	
	@Transactional(readOnly = true)
	List<PaymentNotificationEntity> findByInstitutionId(String clientID);
}
