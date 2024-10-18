package com.bank.services.pmtn.configs;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.bank.services.api.core.BankClient;
import com.bank.services.pmtn.persistence.BankClientEntity;

@Mapper(componentModel = "spring")
public interface BankClientMapper {
	
	/*
	@Mappings({
		@Mapping(target = "clientID", source="entity.clientId"),
		@Mapping(target = "institution", source="entity.institutionName"),
		@Mapping(target = "validationUrl", source="entity.validationEndpoint"),
		@Mapping(target = "paymentNotificationUrl", source="entity.paymentNotificationEndpoint")
	})
	BankClient entityToApi(BankClientMapper entity);
	
	// clientID, institution, validationUrl, paymentNotificationUrl"
	
	@Mappings({
		@Mapping(target = "clientId", source="api.clientID"),
		@Mapping(target = "institutionName", source="api.institution"),
		@Mapping(target = "validationEndpoint", source="api.validationUrl"),
		@Mapping(target = "paymentNotificationEndpoint", source="entity.paymentNotificationEndpoint")
	})
	BankClientEntity apiToEntity(BankClientMapper api);
	
	List<BankClient> entityListToApiList(List<BankClientEntity> entity);
	
	List<BankClientEntity> apiListToEntityList(List<BankClient> api);
	*/
	
	@Mappings({
		@Mapping(target = "clientID", source = "entity.clientId"),
        @Mapping(target = "institution", source = "entity.institutionName"),
        @Mapping(target = "validationUrl", source = "entity.validationEndpoint"),
        @Mapping(target = "paymentNotificationUrl", source = "entity.paymentNotificationEndpoint")
	})
    BankClient entityToApi(BankClientEntity entity);
    
    @Mappings({
        @Mapping(target = "clientId", source = "api.clientID"),
        @Mapping(target = "institutionName", source = "api.institution"),
        @Mapping(target = "validationEndpoint", source = "api.validationUrl"),
        @Mapping(target = "paymentNotificationEndpoint", source = "api.paymentNotificationUrl")
    })
    BankClientEntity apiToEntity(BankClient api);
    
    List<BankClient> entityListToApiList(List<BankClientEntity> entityList);
    
    List<BankClientEntity> apiListToEntityList(List<BankClient> apiList);
}
