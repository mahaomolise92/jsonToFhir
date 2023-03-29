package org.example;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.hapi.fluentpath.FhirPathR4;
import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.JsonParser;
import org.hl7.fhir.r5.hapi.fhirpath.FhirPathR5;
import org.json.JSONArray;
import org.json.JSONObject;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.Element;
import ca.uhn.fhir.fhirpath.IFhirPath;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.text.WordUtils.capitalizeFully;

public class Main {
    public static void main(String[] args) {
        PatientRecord patientRecord = new PatientRecord(
                "patient-123",
                "John",
                "Doe",
                "Male",
                "1980-01-01",
                "New York",
                "+1-555-555-5555",
                "1234567890"
        );

        String configReference = "{\"fields\": [{\"fieldName\": \"patientId\",\"fhirPath\": \"Patient.identifier\"},{\"fieldName\": \"given_name\",\"fhirPath\": \"Patient.name.given\"},{\"fieldName\": \"family_name\",\"fhirPath\": \"Patient.name.family\"},{\"fieldName\": \"gender\",\"fhirPath\": \"Patient.gender\"},{\"fieldName\": \"dob\",\"fhirPath\": \"Patient.birthDate\"},{\"fieldName\": \"city\",\"fhirPath\": \"Patient.address.city\"}]}";
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
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);
        bundle.addEntry().setResource(patient).getRequest().setMethod(Bundle.HTTPVerb.POST).setUrl("Patient");
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            System.out.println(entry.getResource().getClass().getSimpleName() + " ID: " + entry.getResource().getId());
        }


    }

    public static Patient mapToPatientFhir(PatientRecord patientRecord, String configReference) {
        JSONObject config = new JSONObject(configReference);
        Patient patient = new Patient();
        FhirContext fhirContext = FhirContext.forR4();

        for (Field field : patientRecord.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            String fieldValue = null;
            try {
                fieldValue = (String) field.get(patientRecord);
//                System.out.println(fieldValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (fieldValue != null) {
                String fhirPath = getFhirPath(fieldName, config);
//                System.out.println("Path " + fhirPath);
                if (fhirPath != null) {
                    if (fhirPath.equals("Patient.identifier")) {

                        Identifier identifier = new Identifier();
                        identifier.setValue(fieldValue);
                        patient.addIdentifier(identifier);
                        System.out.println("hela Mahao " + identifier);
                    } else {
                        List<Base> values = new ArrayList<>();
                        values.add(new StringType(fieldValue));
//                        System.out.println(values);
                        IFhirPath fhirPathElement = fhirContext.newFhirPath();
                        IParser parser = fhirContext.newJsonParser();
                        List<IBase> element = fhirPathElement.evaluate(patient, fhirPath, IBase.class);
                        System.out.println(fhirPath);
                        if (element != null) {
                            Extension extension = new Extension();
                            extension.setUrl(fhirPath);
                            extension.setValue(new StringType(fieldValue));
//                             element.getExtension().add(extension);
                        }
                    }
                }
            }
        }

        return patient;
    }




    private static String getFhirPath(String fieldName, JSONObject config) {
        JSONArray fields = config.getJSONArray("fields");
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
//            System.out.println(capitalizeFully(field.getString("fieldName")) + " " + fieldName);
            if (fieldName.equalsIgnoreCase(capitalizeFully(field.getString("fieldName")).replace("_", ""))) {
                return field.getString("fhirPath");
            }
        }
        return null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PatientRecord(
            String patientId,
            String givenName,
            String familyName,
            String gender,
            String dob,
            String city,
            String phoneNumber,
            String nationalId) {


    }
}