package edu.xyz.services.gateway.api;

import static java.util.logging.Level.FINE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;


import edu.xyz.services.api.gateway.StudentInfo;
import edu.xyz.services.api.gateway.XyzIntegrationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class XyzIntegrationServiceImpl implements XyzIntegrationService {
	
	private final Xyz_ServicesIntegration integration;
	
	//private final ObjectMapper mapper;
	
	// Convert Student to StudentInfo and vice versa
	private final StudentInfoMapper studentMapper;
	
	//private final Scheduler publishEventScheduler;
	
	// Define a stream bridge that will send messages out to the messaging systems
	//private final StreamBridge streamBridge;
	
	private static final Logger LOG = LoggerFactory.getLogger(XyzIntegrationServiceImpl.class);
	
	@Autowired
	public XyzIntegrationServiceImpl(Xyz_ServicesIntegration integration, StudentInfoMapper studentMapper) {
		this.integration = integration;
		this.studentMapper = studentMapper;
	}
	
	@Override
	public Mono<StudentInfo> getStudentInfo(String studentId) {
		LOG.info("Will get Student info for Student ID={}", studentId);
		
		// return Mono.create(integration.getStudent(studentId)).doOnError(ex -> LOG.warn("Getting Student Info failed"));
		
		return integration.getStudent(studentId)
				.map(student -> studentMapper.studentToApi(student))
				.doOnError(ex -> LOG.warn("Getting Student Info failed: {}", ex.getMessage()))
				.log(LOG.getName(), FINE);
	}

	@Override
	public Mono<StudentInfo> addStudentInfo(StudentInfo body) {
		
		return integration.addStudent(studentMapper.apiToStudent(body))
				.map(student -> studentMapper.studentToApi(student))
				.doOnError(ex -> LOG.warn("Adding a Student failed: {}", ex.getMessage()))
				.log(LOG.getName(), FINE);
	}

	@Override
	public Flux<StudentInfo> getStudentInfoList() {
		LOG.info("Fetching list of students");
		
		return integration.getStudentList()
				.map(student -> studentMapper.studentToApi(student))
				.doOnError(ex -> LOG.warn("Fetching Student list failed: {}", ex.getMessage()))
				.log(LOG.getName(), FINE);
				
	}
}