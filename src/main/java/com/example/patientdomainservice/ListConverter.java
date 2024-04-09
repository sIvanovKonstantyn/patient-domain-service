package com.example.patientdomainservice;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;

public class ListConverter implements AttributeConverter<Set<Object>, String> {
    private ObjectMapper mapper = new ObjectMapper();
    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Set<Object> attribute) {
        return mapper.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public Set<Object> convertToEntityAttribute(String dbData) {
        return mapper.readValue(dbData,  mapper.getTypeFactory()
                                            .constructCollectionType(Set.class, Object.class));
    }
}
