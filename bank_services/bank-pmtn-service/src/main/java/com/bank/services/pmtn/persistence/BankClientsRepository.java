package com.bank.services.pmtn.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BankClientsRepository extends CrudRepository<BankClientEntity, String> {
	
	@Transactional(readOnly = true)
	Optional<BankClientEntity> findByClientId(String clientID);
}
