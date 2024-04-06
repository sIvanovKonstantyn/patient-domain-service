package com.example.patientdomainservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    @EntityGraph("patient-entity-graph")
    Page<Patient> findAllByName(String name, Pageable pageable);
}
