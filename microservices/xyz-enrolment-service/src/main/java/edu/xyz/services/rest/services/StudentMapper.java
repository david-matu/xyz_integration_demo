package edu.xyz.services.rest.services;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import edu.xyz.services.api.core.enrolment.Student;
import edu.xyz.services.rest.persistence.StudentEntity;

/**
 * This interface will provide conversion of StudentEntity from db to a client facing object which is Student
 * This is a utility type whose implementation will be generated at compile time
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {
	
	@Mappings({
		@Mapping(target = "serviceAddress", ignore = true)
	})
	Student entityToApi(StudentEntity entity);
	
	
	@Mappings({
		@Mapping(target = "accountNumber", ignore = true),
		@Mapping(target = "status", ignore = true)
	})
	StudentEntity apiToEntity(Student api);
	
	
	List<Student> entityListToApiList(List<StudentEntity> entity);
	
	List<StudentEntity> apiListToEntityList(List<Student> api);
}
