package com.example.patientdomainservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Telecom {
    private String telecomText;
    private Integer telecomCode;
}
