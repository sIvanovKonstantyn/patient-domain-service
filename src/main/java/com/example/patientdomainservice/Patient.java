package com.example.patientdomainservice;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(indexes = @Index(columnList = "name"))
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean active;
    private String name;
    @Convert(converter = ListConverter.class)
    private Set<Telecom> telecom;
    @Convert(converter = ListConverter.class)
    private Set<Address> addresses;
    private LocalDate birthDate;
    private Gender gender;
    private byte[] photo;
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization managingOrganization;
}
