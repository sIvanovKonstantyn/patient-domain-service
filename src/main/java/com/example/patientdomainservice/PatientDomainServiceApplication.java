package com.example.patientdomainservice;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PatientDomainServiceApplication {
	@Autowired
	private PatientRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(PatientDomainServiceApplication.class, args);
	}

	@PostMapping
	public Patient createPatient(@RequestBody Patient patient) {
		patient.setLongText(buildRandomLongText());
        return repository.save(patient);
    }

	private String buildRandomLongText() {
		var sb = new StringBuilder();
		for (int i = 0; i < 10000; i++)
			sb.append(UUID.randomUUID());
		return sb.toString();
	}

	@GetMapping
	public List<Patient> getPatients() {
        return repository.findAll();
    }
}
