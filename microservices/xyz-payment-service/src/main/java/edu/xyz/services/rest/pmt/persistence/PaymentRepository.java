package edu.xyz.services.rest.pmt.persistence;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public interface PaymentRepository extends CrudRepository<PaymentEntity, String>{
	
	@Transactional(readOnly = true)
	PaymentEntity findByStudentID(String studentId);
	
	@Transactional(readOnly = true)
	PaymentEntity findByPaymentID(String paymentId);
	
	@Transactional(readOnly = true)
	PaymentEntity findByExternalReference(String exRef);
	
	@Transactional(readOnly = true)
	List<PaymentEntity> findAll();
}
