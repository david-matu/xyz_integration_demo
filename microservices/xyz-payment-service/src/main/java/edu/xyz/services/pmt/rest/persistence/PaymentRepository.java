package edu.xyz.services.pmt.rest.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends CrudRepository<PaymentEntity, Long>{
	
	@Transactional(readOnly = true)
	List<PaymentEntity> findByStudentId(String studentId);
	
	@Transactional(readOnly = true)
	Optional<PaymentEntity> findByPaymentId(long paymentId);
	
	@Transactional(readOnly = true)
	Optional<PaymentEntity> findByExternalReference(String exRef);
	
	@Transactional(readOnly = true)
	List<PaymentEntity> findAll();
}
