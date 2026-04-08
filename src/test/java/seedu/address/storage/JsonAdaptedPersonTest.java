package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";

    private static final String VALID_TYPE = "person"; // added by Copilot
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, INVALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, null,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, VALID_NAME,
            INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, VALID_NAME,
            null, VALID_EMAIL, VALID_ADDRESS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, VALID_NAME,
            VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, null, null);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, VALID_NAME,
            VALID_PHONE, null, VALID_ADDRESS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, VALID_NAME,
            VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, null, null);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_TYPE, VALID_NAME,
            VALID_PHONE, VALID_EMAIL, null, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_doctorWithDocId_savesDocIdSucessfully() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson("doctor", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, 12, null);
        Person doctor = person.toModelType();
        assertTrue(doctor instanceof Doctor);
        assertEquals(12, ((Doctor) doctor).getDocId());
    }

    @Test
    public void toModelType_doctorWithoutDocId_generatesId() throws Exception {
        Doctor.resetIdTracker();
        JsonAdaptedPerson person = new JsonAdaptedPerson("doctor", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        Person doctor = person.toModelType();
        assertTrue(doctor instanceof Doctor);
        assertEquals(1, ((Doctor) doctor).getDocId());
    }

    @Test
    public void toModelType_patientType_returnsPatient() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null); // checking docId
        Person patient = person.toModelType();
        assertTrue(patient instanceof Patient);
    }

    @Test
    public void toModelType_patientWithPatientId_savesPatientIdSuccessfully() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, 5);
        Person patient = person.toModelType();
        assertTrue(patient instanceof Patient);
        assertEquals(5, ((Patient) patient).getPatientId());
    }

    @Test
    public void toModelType_patientWithoutPatientId_generatesId() throws Exception {
        Patient.resetIdTracker();
        JsonAdaptedPerson person = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        Person patient = person.toModelType();
        assertTrue(patient instanceof Patient);
        assertEquals(1, ((Patient) patient).getPatientId());
    }

    @Test
    public void toModelType_multiplePatients_generatesUniqueIds() throws Exception {
        Patient.resetIdTracker();

        JsonAdaptedPerson person1 = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        JsonAdaptedPerson person2 = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        JsonAdaptedPerson person3 = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);

        Patient p1 = (Patient) person1.toModelType();
        Patient p2 = (Patient) person2.toModelType();
        Patient p3 = (Patient) person3.toModelType();

        assertEquals(1, p1.getPatientId());
        assertEquals(2, p2.getPatientId());
        assertEquals(3, p3.getPatientId());
    }

    @Test
    public void toModelType_patientIdFromSchedule_returnsCorrectFormat() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, 7);
        Patient patient = (Patient) person.toModelType();
        assertEquals("pat_7", patient.getPatientIdFromSchedule());
    }

    @Test
    public void toModelType_setIdTracker_setsNextPatientId() throws Exception {
        Patient.resetIdTracker();
        Patient.setIdTracker(10);

        JsonAdaptedPerson person = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        Patient patient = (Patient) person.toModelType();

        assertEquals(10, patient.getPatientId());
    }

    @Test
    public void jsonAdaptedPerson_convertPatientToJson_preservesPatientId() throws Exception {
        Patient.resetIdTracker();
        Patient original = new Patient(new Name(VALID_NAME), new Phone(VALID_PHONE),
            new Email(VALID_EMAIL), new Address(VALID_ADDRESS), 15);

        JsonAdaptedPerson adapted = new JsonAdaptedPerson(original);
        Person restored = adapted.toModelType();

        assertTrue(restored instanceof Patient);
        assertEquals(15, ((Patient) restored).getPatientId());
    }

    @Test
    public void jsonAdaptedPerson_patientWithAutoGeneratedId_preservesId() throws Exception {
        Patient.resetIdTracker();
        Patient original = new Patient(new Name(VALID_NAME), new Phone(VALID_PHONE),
            new Email(VALID_EMAIL), new Address(VALID_ADDRESS));
        int originalId = original.getPatientId();

        JsonAdaptedPerson adapted = new JsonAdaptedPerson(original);
        Patient restored = (Patient) adapted.toModelType();

        assertEquals(originalId, restored.getPatientId());
    }

    @Test
    public void toModelType_patientWithZeroId_savesSuccessfully() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, 0);
        Person patient = person.toModelType();
        assertTrue(patient instanceof Patient);
        assertEquals(0, ((Patient) patient).getPatientId());
    }

    @Test
    public void toModelType_patientWithLargeId_savesSuccessfully() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, 999999);
        Person patient = person.toModelType();
        assertTrue(patient instanceof Patient);
        assertEquals(999999, ((Patient) patient).getPatientId());
    }

    @Test
    public void toModelType_consecutivePatientIds_incrementsProperly() throws Exception {
        Patient.resetIdTracker();

        JsonAdaptedPerson[] persons = new JsonAdaptedPerson[5];
        Patient[] patients = new Patient[5];

        for (int i = 0; i < 5; i++) {
            persons[i] = new JsonAdaptedPerson("patient", VALID_NAME,
                VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
            patients[i] = (Patient) persons[i].toModelType();
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, patients[i].getPatientId());
        }
    }

    @Test
    public void toModelType_patientAndDoctorIds_areIndependent() throws Exception {
        Patient.resetIdTracker();
        Doctor.resetIdTracker();

        JsonAdaptedPerson patient = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        JsonAdaptedPerson doctor = new JsonAdaptedPerson("doctor", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);

        Patient p = (Patient) patient.toModelType();
        Doctor d = (Doctor) doctor.toModelType();

        assertEquals(1, p.getPatientId());
        assertEquals(1, d.getDocId());
    }

    @Test
    public void toModelType_resetIdTracker_resetsPatientIdCounter() throws Exception {
        Patient.resetIdTracker();

        JsonAdaptedPerson person1 = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        Patient p1 = (Patient) person1.toModelType();
        assertEquals(1, p1.getPatientId());

        Patient.resetIdTracker();

        JsonAdaptedPerson person2 = new JsonAdaptedPerson("patient", VALID_NAME,
            VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, null);
        Patient p2 = (Patient) person2.toModelType();
        assertEquals(1, p2.getPatientId());
    }
}

