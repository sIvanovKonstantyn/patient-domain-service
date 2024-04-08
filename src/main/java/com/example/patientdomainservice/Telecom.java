package com.example.patientdomainservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Telecom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long telecomId;
    private String telecomText;
    private Integer telecomCode;
}
