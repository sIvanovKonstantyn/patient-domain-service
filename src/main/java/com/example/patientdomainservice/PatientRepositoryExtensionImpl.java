package com.example.patientdomainservice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientRepositoryExtensionImpl implements PatientRepositoryExtension {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Patient> findAllLimited(Integer pageNumber, Integer pageSize) {
        var query = """
            select
                p1_0.id,
                p1_0.active,
                p1_0.name,
                p1_0.birth_date,
                p1_0.gender,
                p1_0.photo,
                a1_0.address_id,
                a1_0.address_text,
                mo1_0.organization_id,
                mo1_0.organization_name,
                t1_0.telecom_id,
                t1_0.telecom_text,
                t1_0.telecom_code
            from
                patient p1_0
            left join
                address a1_0
                    on p1_0.id=a1_0.patient_id
            join
                organization mo1_0
                    on mo1_0.organization_id=p1_0.organization_id
            left join
                telecom t1_0
                    on p1_0.id=t1_0.patient_id
            LIMIT ?
            OFFSET ?\s
            """;
        List<Patient> result = new ArrayList<>();

        try(var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(query)) {
            statement.setInt(1, pageSize);
            statement.setInt(2, pageNumber);

            var resultSet = statement.executeQuery();
            Patient currentPatient = null;
            Patient lastAddedPatient = null;
            Long currentId = null;

            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                if(currentId == null || currentId != id) {
                    //new patient
                    if (currentPatient != null) {
                        //we have current patient - add it to result
                        result.add(currentPatient);
                        lastAddedPatient = currentPatient;
                    }
                    currentPatient = new Patient();
                    populatePatientFields(currentPatient, id, resultSet);
                    populateOrganizationFields(currentPatient, resultSet);

                    currentId = id;
                }
                populateAddressFields(currentPatient, resultSet);
                populateTelecomFields(currentPatient, resultSet);
            }

            if (currentPatient != null && (lastAddedPatient == null || lastAddedPatient != currentPatient)) {
                result.add(currentPatient);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    private void populateOrganizationFields(Patient currentPatient, ResultSet resultSet) throws SQLException {
        if (currentPatient.getManagingOrganization() == null) {
            currentPatient.setManagingOrganization(new Organization(
                resultSet.getLong(9), resultSet.getString(10)));
        }
    }
    private void populateTelecomFields(Patient currentPatient, ResultSet resultSet) throws SQLException {
        if (currentPatient.getTelecom() == null) {
            currentPatient.setTelecom(new HashSet<>());
        }
        currentPatient.getTelecom().add(
            new Telecom(resultSet.getLong(11), resultSet.getString(12),
                resultSet.getInt(13)));
    }
    private void populateAddressFields(Patient currentPatient, ResultSet resultSet) throws SQLException {
        if (currentPatient.getAddresses() == null) {
            currentPatient.setAddresses(new HashSet<>());
        }
        currentPatient.getAddresses().add(
            new Address(resultSet.getLong(7), resultSet.getString(8)));
    }

    private void populatePatientFields(Patient currentPatient, long id, ResultSet resultSet) throws SQLException {
        currentPatient.setId(id);

        currentPatient.setActive(resultSet.getBoolean(2));
        currentPatient.setName(resultSet.getString(3));
        currentPatient.setBirthDate(resultSet.getDate(4)
            .toLocalDate());

        currentPatient.setGender(Gender.valueOnIndex(resultSet.getInt(5)));
        currentPatient.setPhoto(resultSet.getBytes(6));
    }
}
