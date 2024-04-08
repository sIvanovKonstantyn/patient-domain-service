package com.example.patientdomainservice;

public enum Gender {
    MALE, FEMALE, OTHER;

    public static Gender valueOnIndex(int i) {
        return Gender.values()[i];
    }
}
