package edu.xyz.services.pmt.rest.services;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.bank.services.api.PaymentNotificationDetails;

import edu.xyz.services.api.gateway.payments.Payment;
import edu.xyz.services.pmt.rest.persistence.PaymentEntity;

/**
 * This interface will provide conversion of PaymentEntity from db to a client facing object which is Payment
 * This is a utility type whose implementation will be generated at compile time
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {
	
	Payment entityToApi(PaymentEntity entity);
	
	PaymentEntity apiToEntity(Payment api);
	
	@Mappings({
        @Mapping(source = "paymentReference", target = "externalReference"),
        @Mapping(source = "studentId", target = "studentId"),
        @Mapping(source = "amountPaid", target = "amounPaid"),
        @Mapping(ignore = true, target = "paymentID"),
        @Mapping(ignore = true, target = "forInvoiceId"),
        @Mapping(ignore = true, target = "comment")
    })
	PaymentEntity notifDetailsToEntity(PaymentNotificationDetails details);
	
	@Mappings({
        @Mapping(source = "paymentID", target = "paymentReference"),
        @Mapping(source = "studentId", target = "studentId"),
        @Mapping(source = "amounPaid", target = "amountPaid"),
        @Mapping(source = "accountNumber", target = "accountNumber"),
        @Mapping(ignore = true, target = "branch"),
        @Mapping(ignore = true, target = "currency"),
        @Mapping(ignore = true, target = "firstName"),
        @Mapping(ignore = true, target = "lastName"),
    })
	PaymentNotificationDetails entityToNotifDetails(PaymentEntity entity);
	
	List<Payment> entityListToApiList(List<PaymentEntity> entity);
	
	List<PaymentEntity> apiListToEntityList(List<Payment> api);

	List<PaymentNotificationDetails> entityListToNotifList(List<PaymentEntity> entity);
	
	List<PaymentEntity> notifListToEntityList(List<PaymentNotificationDetails> api);
}
