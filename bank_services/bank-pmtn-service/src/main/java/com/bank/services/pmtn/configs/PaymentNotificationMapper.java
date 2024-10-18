package com.bank.services.pmtn.configs;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.bank.services.api.PaymentNotification;
import com.bank.services.api.PaymentNotificationDetails;
import com.bank.services.pmtn.persistence.PaymentNotificationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Mapper(componentModel = "spring")
public interface PaymentNotificationMapper {
	
	// "queuedAt, sent, sentAt, acknowledged, acknowledgementResponse, sendCount, commentLog".
	/*
	@Mappings({
		@Mapping(ignore = true, target="queuedAt"),
		@Mapping(ignore = true, target="sent"),
		@Mapping(ignore = true, target="sentAt"),
		@Mapping(ignore = true, target="acknowledged"),
		@Mapping(ignore = true, target="acknowledgementResponse"),
		@Mapping(ignore = true, target="sendCount"),
		@Mapping(ignore = true, target="commentLog"),
	})
	*
	PaymentNotification entityToApi(PaymentNotificationEntity entity);
	
	
	PaymentNotificationEntity apiToEntity(PaymentNotification api);
	
	
	List<PaymentNotification> entityListToApiList(List<PaymentNotificationEntity> entityList);
	
	List<PaymentNotificationEntity> apiListToEntityList(List<PaymentNotification> apiList);
	
	PaymentNotificationDetails map(String value);
	
	/*
	 * @Mappings({
		@Mapping(source = "clientId", target="api.institutionId"),
		@Mapping(source = "institutionName", target="api.institution")
	})
	 * @Mapping(ignore = true, target="queuedAt"),
		@Mapping(ignore = true, target="sent"),
		@Mapping(ignore = true, target="sentAt"),
		@Mapping(ignore = true, target="acknowledged"),
		@Mapping(ignore = true, target="acknowledgementResponse"),
		@Mapping(ignore = true, target="sendCount"),
		@Mapping(ignore = true, target="commentLog"),
	 */
	
	// Mapping for PaymentNotificationEntity to PaymentNotification
    @Mappings({
        @Mapping(source = "paymentDetails", target = "paymentDetails", qualifiedByName = "stringToPaymentNotificationDetails")
    })
    PaymentNotification entityToApi(PaymentNotificationEntity entity);
    
    // Mapping for PaymentNotification to PaymentNotificationEntity
    @Mappings({
        @Mapping(source = "paymentDetails", target = "paymentDetails", qualifiedByName = "paymentNotificationDetailsToString"),
        @Mapping(ignore = true, target="queuedAt"),
		@Mapping(ignore = true, target="sent"),
		@Mapping(ignore = true, target="sentAt"),
		@Mapping(ignore = true, target="acknowledged"),
		@Mapping(ignore = true, target="acknowledgementResponse"),
		@Mapping(ignore = true, target="sendCount"),
		@Mapping(ignore = true, target="commentLog")
    })
    PaymentNotificationEntity apiToEntity(PaymentNotification api);
    
    // List mappings
    List<PaymentNotification> entityListToApiList(List<PaymentNotificationEntity> entityList);
    List<PaymentNotificationEntity> apiListToEntityList(List<PaymentNotification> apiList);
    
    // Custom mapping for PaymentNotificationDetails -> String
    @Named("paymentNotificationDetailsToString")
    default String paymentNotificationDetailsToString(PaymentNotificationDetails details) {
        // Assuming you want to convert it to JSON
        try {
            return new ObjectMapper().writeValueAsString(details);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting PaymentNotificationDetails to JSON", e);
        }
    }

    // Custom mapping for String -> PaymentNotificationDetails
    @Named("stringToPaymentNotificationDetails")
    default PaymentNotificationDetails stringToPaymentNotificationDetails(String value) {
        try {
            return new ObjectMapper().readValue(value, PaymentNotificationDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to PaymentNotificationDetails", e);
        }
    }
}

