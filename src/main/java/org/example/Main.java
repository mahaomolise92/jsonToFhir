package org.example;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.Create;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.utils.FHIRPathEngine;
import org.json.JSONArray;
import org.json.JSONObject;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.text.WordUtils.capitalizeFully;

public class Main {
    public static void main(String[] args) {
        CustomDemographicData demographicData = new CustomDemographicData(
                "123",
                "John",
                "Doe",
                "Male",
                "1980-01-01",
                "New York",
                "555-555-5555",
                "ABC123"
        );

        SourceId sourceId = new SourceId(
                "123",
                "Some Facility",
                "Some Patient"
        );

        PatientRecord patientRecord = new PatientRecord(
                "456",
                sourceId,
                demographicData
        );

//        String configReference = "{\"fields\": [{\"fieldName\": \"patientId\",\"fhirPath\": \"Patient.identifier\"},{\"fieldName\": \"given_name\",\"fhirPath\": \"name.given\"},{\"fieldName\": \"family_name\",\"fhirPath\": \"Patient.name.family\"},{\"fieldName\": \"gender\",\"fhirPath\": \"Patient.gender\"},{\"fieldName\": \"dob\",\"fhirPath\": \"Patient.birthDate\"},{\"fieldName\": \"city\",\"fhirPath\": \"Patient.address.city\"}]}";
        String conf = "{\"fields\": [\n" +
                "    {\n" +
                "      \"fieldName\": \"aux_id\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"indexGoldenRecord\": \"@index(exact)\",\n" +
                "      \"fieldLabel\": \"AUX ID\",\n" +
                "      \"groups\": [\"identifiers\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/search/custom\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"extension.where(url = 'http://example.com/fhir/extensions#aux_id').value\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"fieldName\": \"given_name\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"indexGoldenRecord\": \"@index(exact,trigram)\",\n" +
                "      \"indexEntity\": \"@index(exact,trigram)\",\n" +
                "      \"m\": 0.782501,\n" +
                "      \"u\": 0.02372,\n" +
                "      \"fieldLabel\": \"First Name\",\n" +
                "      \"groups\": [\"name\", \"demographics\", \"linked_records\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/match-details\",\n" +
                "        \"/golden-record/:uid/linked-records\",\n" +
                "        \"/golden-record/:uid/audit-trail\",\n" +
                "        \"/search/simple\",\n" +
                "        \"/search/custom\",\n" +
                "        \"/search-results/golden\",\n" +
                "        \"/search-results/patient\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"name.given\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"fieldName\": \"family_name\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"indexGoldenRecord\": \"@index(exact,trigram)\",\n" +
                "      \"indexEntity\": \"@index(exact,trigram)\",\n" +
                "      \"m\": 0.850909,\n" +
                "      \"u\": 0.02975,\n" +
                "      \"fieldLabel\": \"Last Name\",\n" +
                "      \"groups\": [\"name\", \"demographics\", \"linked_records\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/match-details\",\n" +
                "        \"/golden-record/:uid/linked-records\",\n" +
                "        \"/golden-record/:uid/audit-trail\",\n" +
                "        \"/search/simple\",\n" +
                "        \"/search/custom\",\n" +
                "        \"/search-results/golden\",\n" +
                "        \"/search-results/patient\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"name.family\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"fieldName\": \"gender\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"indexGoldenRecord\": \"@index(exact)\",\n" +
                "      \"m\": 0.786614,\n" +
                "      \"u\": 0.443018,\n" +
                "      \"fieldLabel\": \"Gender\",\n" +
                "      \"groups\": [\"demographics\", \"sub_heading\", \"linked_records\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/match-details\",\n" +
                "        \"/golden-record/:uid/linked-records\",\n" +
                "        \"/search/custom\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"gender\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"fieldName\": \"dob\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"m\": 0.894637,\n" +
                "      \"u\": 0.012448,\n" +
                "      \"fieldLabel\": \"Date of Birth\",\n" +
                "      \"groups\": [\"demographics\", \"sub_heading\", \"linked_records\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/match-details\",\n" +
                "        \"/golden-record/:uid/linked-records\",\n" +
                "        \"/search/simple\",\n" +
                "        \"/search/custom\",\n" +
                "        \"/search-results/golden\",\n" +
                "        \"/search-results/patient\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"birthDate\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"fieldName\": \"city\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"indexGoldenRecord\": \"@index(trigram)\",\n" +
                "      \"m\": 0.872691,\n" +
                "      \"u\": 0.132717,\n" +
                "      \"fieldLabel\": \"City\",\n" +
                "      \"groups\": [\"demographics\", \"linked_records\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/match-details\",\n" +
                "        \"/golden-record/:uid/linked-records\",\n" +
                "        \"/search/custom\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"address.city\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"fieldName\": \"phone_number\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"indexGoldenRecord\": \"@index(exact,trigram)\",\n" +
                "      \"m\": 0.920281,\n" +
                "      \"u\": 0.322629,\n" +
                "      \"fieldLabel\": \"Phone No\",\n" +
                "      \"groups\": [\"demographics\", \"linked_records\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/match-details\",\n" +
                "        \"/golden-record/:uid/linked-records\",\n" +
                "        \"/search/custom\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"telecom.where(system = 'phone').value\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"fieldName\": \"national_id\",\n" +
                "      \"fieldType\": \"String\",\n" +
                "      \"indexGoldenRecord\": \"@index(exact,trigram)\",\n" +
                "      \"indexEntity\": \"@index(exact,trigram)\",\n" +
                "      \"m\": 0.832336,\n" +
                "      \"u\": 0.000133,\n" +
                "      \"fieldLabel\": \"National ID\",\n" +
                "      \"groups\": [\"identifiers\", \"linked_records\"],\n" +
                "      \"scope\": [\n" +
                "        \"/patient-record/:uid\",\n" +
                "        \"/golden-record/:uid\",\n" +
                "        \"/match-details\",\n" +
                "        \"/golden-record/:uid/linked-records\",\n" +
                "        \"/search/simple\",\n" +
                "        \"/search/custom\",\n" +
                "        \"/search-results/golden\",\n" +
                "        \"/search-results/patient\"\n" +
                "      ],\n" +
                "      \"accessLevel\": [],\n" +
                "      \"fhirPath\": \"Patient.identifier\"\n" +
                "    }\n" +
                "  ]}";
            Patient patient = mapToPatientFhir(patientRecord, conf);
//             Create a FHIR JSON parser
            FhirContext ctx = FhirContext.forR4();

            // Serialize the patient object to FHIR JSON
            String patientJson = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);

            // Display the FHIR JSON string
            System.out.println(patientJson);


    }

    public static Patient mapToPatientFhir(PatientRecord patientRecord, String configReference) {
        JSONObject config = new JSONObject(configReference);
        Patient patient = new Patient();
        FhirContext fhirContext = FhirContext.forR4();
        CustomDemographicData demographicData = patientRecord.demographicData();

        for (Field field : PatientRecord.class.getDeclaredFields()) {
            String fieldName = field.getName();
            String fieldValue = null;
            try {
                if (fieldName.equals("demographicData")) {
                    for (Field demoField : CustomDemographicData.class.getDeclaredFields()) {
                        String demoFieldName = demoField.getName();
                        fieldValue = (String) demoField.get(demographicData);
                        if (fieldValue != null) {
                            String fhirPath = getFhirPath(demoFieldName, config);
                            if (fhirPath != null) {
                                processField(patient, fieldValue, fhirPath);
                            }
                        }
                    }
                } else if (fieldName.equals("patientId")) {
                    fieldValue = (String) field.get(patientRecord);
                    System.out.println("this is a patient Id " + fieldValue);
                    Identifier identifier = new Identifier();
                    identifier.setValue(fieldValue);
                    patient.addIdentifier(identifier);

                } else if (fieldName.equals("sourceId")) {
                        //to be implemented
                } else {
                    fieldValue = (String) field.get(patientRecord);
                    if (fieldValue != null) {
                        System.out.println(fieldName + " " + fieldValue);
                        String fhirPath = getFhirPath(fieldName, config);
                        if (fhirPath != null) {
                            processField(patient, fieldValue, fhirPath);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return patient;
    }

    private static void processField(Patient patient, String fieldValue, String fhirPath) {
        switch (fhirPath) {
            case "Patient.identifier":
                Identifier identifier = new Identifier();
                identifier.setValue(fieldValue);
                patient.addIdentifier(identifier);
                break;
            case "name.given":
                HumanName name = new HumanName();
                name.addGiven(fieldValue);
                patient.addName(name);
                break;
            case "name.family":
                HumanName namee = new HumanName();
                namee.setFamily(fieldValue);
                patient.addName(namee);
                break;
            case "address.city":
                Address address = new Address();
                address.setCity(fieldValue);
                patient.addAddress(address);
                break;
            case "birthDate":
                DateType birthDate = new DateType(fieldValue);
                patient.setBirthDateElement(birthDate);
                break;
            default:
                List<Base> values = new ArrayList<>();
                values.add(new StringType(fieldValue));
                IFhirPath fhirPathElement = FhirContext.forR4().newFhirPath();
                IParser parser = FhirContext.forR4().newJsonParser();
                List<IBase> elements = fhirPathElement.evaluate(patient, fhirPath, IBase.class);
                break;
        }
    }

    private static String getFhirPath(String fieldName, JSONObject config) {
        JSONArray fields = config.getJSONArray("fields");
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            if (fieldName.equalsIgnoreCase(capitalizeFully(field.getString("fieldName")).replace("_", ""))) {
                return field.getString("fhirPath");
            }
        }
        return null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CustomDemographicData(
            String auxId,
            String givenName,
            String familyName,
            String gender,
            String dob,
            String city,
            String phoneNumber,
            String nationalId) {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PatientRecord(
            String patientId,
            SourceId sourceId,
            CustomDemographicData demographicData) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SourceId(
            String uid,
            String facility,
            String patient) {
    }

}