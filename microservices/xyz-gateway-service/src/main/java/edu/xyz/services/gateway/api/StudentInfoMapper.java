package edu.xyz.services.gateway.api;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import edu.xyz.services.api.core.enrolment.Student;
import edu.xyz.services.api.gateway.StudentInfo;

@Mapper(componentModel = "spring")
public interface StudentInfoMapper {
	
	@Mappings({
		@Mapping(target = "serviceAddress", ignore = true)
	})
	Student apiToStudent(StudentInfo studentInfo);
	
	StudentInfo studentToApi(Student student);
	
	List<Student> apiListToStudentList(List<StudentInfo> studentInfoList);
	
	List<StudentInfo> studentListToApiList(List<Student> studentInfoList);
}
