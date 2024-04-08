package com.example.patientdomainservice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PatientDomainServiceApplication {
	@Autowired
	private PatientRepository repository;
	@Autowired
	private OrganizationRepository organizationRepository;

	public static void main(String[] args) {
		SpringApplication.run(PatientDomainServiceApplication.class, args);
	}

	@PostMapping
	public Patient createPatient(@RequestBody Patient patient) {
		Optional<Organization> foundOrganization = organizationRepository.findOneByOrganizationName(patient.getManagingOrganization()
			.getOrganizationName());

		patient.setManagingOrganization(
			foundOrganization.orElseGet(() -> organizationRepository.save(patient.getManagingOrganization())));

		return repository.save(patient);
    }

	@GetMapping
	public List<Patient> getPatientsByFilters(@RequestParam(required = false) String name,
		@RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize) {

        return name == null
	        ? repository.findAllLimited(pageNumber, pageSize)
	        : repository.findAllByName(name, PageRequest.of(0, 1000)).getContent();
    }
}
