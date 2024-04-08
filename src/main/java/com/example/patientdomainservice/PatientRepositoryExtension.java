package com.example.patientdomainservice;

import java.util.List;

public interface PatientRepositoryExtension {
    List<Patient> findAllLimited(Integer pageNumber, Integer pageSize);
}
