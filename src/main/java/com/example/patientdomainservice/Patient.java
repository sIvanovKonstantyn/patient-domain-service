package com.example.patientdomainservice;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean active;
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id", nullable=false)
    private List<Telecom> telecom;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id", nullable=false)
    private List<Address> addresses;
    private LocalDate birthDate;
    private Gender gender;
    private byte[] photo;
    @ManyToOne
    @JoinColumn(name="organization_id", nullable=false)
    private Organization managingOrganization;
}
