package edu.xyz.services.rest.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface StudentRepository extends CrudRepository<StudentEntity, String>{
	
	@Transactional(readOnly = true)
	StudentEntity findByStudentID(String studentId);
	
	List<StudentEntity> findAll();
}
