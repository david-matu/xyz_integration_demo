package edu.xyz.services.rest.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface StudentRepository extends CrudRepository<StudentEntity, String>{
	
	@Transactional(readOnly = true)
	Optional<StudentEntity> findByStudentId(String studentId);
	
	Optional<StudentEntity> findByStudentIdAndAccountNumber(String studentId, String accountNumber);
	
	List<StudentEntity> findAll();
}
