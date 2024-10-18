package edu.xyz.services.rest.pmt.services;

import java.util.List;

import org.mapstruct.Mapper;

import edu.xyz.services.api.gateway.payments.Payment;
import edu.xyz.services.rest.pmt.persistence.PaymentEntity;

/**
 * This interface will provide conversion of PaymentEntity from db to a client facing object which is Payment
 * This is a utility type whose implementation will be generated at compile time
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {
	
	
	Payment entityToApi(PaymentEntity entity);
	
	PaymentEntity apiToEntity(Payment api);
	
	
	List<Payment> entityListToApiList(List<PaymentEntity> entity);
	
	List<PaymentEntity> apiListToEntityList(List<Payment> api);

}
