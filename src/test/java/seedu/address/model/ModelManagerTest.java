package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_doctorsNotInAddressBook_mergedIntoAddressBook() {
        Doctor doctor = new DoctorBuilder().withName("Grey").withPhone("00121212")
                .withEmail("grey@doc.com").withAddress("20 Haji Lane").build();
        AddressBook newAddressBook = new AddressBook();
        AddressBook newPatientsAddressBook = new AddressBook();
        AddressBook doctorsAddressBook = new AddressBook();
        doctorsAddressBook.addDoctor(doctor);
        ModelManager modelManager = new ModelManager(newAddressBook, newPatientsAddressBook,
                doctorsAddressBook, new UserPrefs());
        assertTrue(modelManager.hasPerson(doctor));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }


    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void setDoctorsFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setDoctorsFilePath(null));
    }

    @Test
    public void setDoctorsFilePath_validPath_setsDoctorsFilePath() {
        Path path = Paths.get("doctors/file/path");
        modelManager.setDoctorsFilePath(path);
        assertEquals(path, modelManager.getDoctorsFilePath());
    }

    @Test
    public void getDoctorData_defaultModelManager_returnsEmptyAddressBook() {
        assertEquals(new AddressBook(), new AddressBook(modelManager.getDoctorData()));
    }

    @Test
    public void hasDoctor_nullDoctor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasDoctor(null));
    }

    @Test
    public void hasDoctor_doctorNotInModel_returnsFalse() {
        Doctor doctor = new DoctorBuilder().withName("Peabody").withPhone("67676767")
                .withEmail("peabody@doc.com").withAddress("Sherman St").build();
        assertFalse(modelManager.hasDoctor(doctor));
    }

    @Test
    public void hasDoctor_doctorInModel_returnsTrue() {
        Doctor doctor = new DoctorBuilder().withName("House").withPhone("76767676")
                .withEmail("house@doc.com").withAddress("Princeton Plainsborough").build();
        modelManager.addDoctor(doctor);
        assertTrue(modelManager.hasDoctor(doctor));
    }

    @Test
    public void addDoctor_validDoctor_addsBoth() {
        Doctor doctor = new DoctorBuilder().withName("Mickey Mouse").withPhone("20202020")
                .withEmail("mickey@doc.com").withAddress("11 Clubhouse St").build();
        modelManager.addDoctor(doctor);

        assertTrue(modelManager.hasDoctor(doctor));
        assertTrue(modelManager.hasPerson(doctor));
    }

    @Test
    public void deleteDoctor_doctorInModel_removesBoth() {
        Doctor doctor = new DoctorBuilder().withName("Nefario").withPhone("82828282")
                .withEmail("nefario@minions.com").withAddress("32 AVL Headquarters").build();
        modelManager.addDoctor(doctor);
        assertTrue(modelManager.hasDoctor(doctor));

        modelManager.deleteDoctor(doctor);
        assertFalse(modelManager.hasDoctor(doctor));
        assertFalse(modelManager.hasPerson(doctor));
    }

    @Test
    public void setDoctor_validEdit_updatesBoth() {
        Doctor oldDoc = new DoctorBuilder().withName("McDonald").withPhone("44445555")
                .withEmail("mcd@doc.com").withAddress("Old Road").build();
        modelManager.addDoctor(oldDoc);

        Doctor newDoc = new DoctorBuilder().withName("Kentucky").withPhone("44445555")
                .withEmail("mcd@doc.com").withAddress("Chicken Road").build();
        modelManager.setDoctor(oldDoc, newDoc);

        assertFalse(modelManager.hasDoctor(oldDoc));
        assertFalse(modelManager.hasPerson(oldDoc));
        assertTrue(modelManager.hasDoctor(newDoc));
        assertTrue(modelManager.hasPerson(newDoc));
    }

    @Test
    public void hasDoctorWithName_doctorExists_returnsTrue() {
        Doctor doctor = new DoctorBuilder().withName("Gru").withPhone("67896789")
                .withEmail("gru@minions.com").withAddress("33 AVL").build();
        modelManager.addDoctor(doctor);
        assertTrue(modelManager.hasDoctorWithName("Gru"));
    }

    @Test
    public void hasDoctorWithName_doctorDoesNotExist_returnsFalse() {
        assertFalse(modelManager.hasDoctorWithName("Invisidoc"));
    }

    @Test
    public void hasDoctorWithName_nameIsCaseInsensitive_returnsTrue() {
        Doctor doctor = new DoctorBuilder().withName("Gru").withPhone("67896789")
                .withEmail("gru@minions.com").withAddress("33 AVL").build();
        modelManager.addDoctor(doctor);
        assertTrue(modelManager.hasDoctorWithName("gru"));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook patients = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook doctors = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        AddressBook differentPatients = new AddressBook();
        AddressBook differentDoctors = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, patients, doctors, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, patients, doctors, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, differentPatients,
                differentDoctors, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, patients, doctors, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, patients, doctors, differentUserPrefs)));
    }

    @Test
    public void getPatientsFilePath_returnsCorrectFilePath() {
        Path expectedPath = Paths.get("data/patients.json");
        modelManager.setPatientsFilePath(expectedPath);
        assertEquals(expectedPath, modelManager.getPatientsFilePath());
    }

    @Test
    public void getDoctorsFilePath_returnsCorrectFilePath() {
        Path expectedPath = Paths.get("data/doctors.json");
        modelManager.setDoctorsFilePath(expectedPath);
        assertEquals(expectedPath, modelManager.getDoctorsFilePath());
    }

    @Test
    public void getScheduleFilePath_returnsCorrectFilePath() {
        Path expectedPath = Paths.get("data/schedule.json");
        modelManager.setScheduleFilePath(expectedPath);
        assertEquals(expectedPath, modelManager.getScheduleFilePath());
    }

    @Test
    public void deletePatient_patientExists_patientDeletedSuccessfully() {
        Patient patient = new PatientBuilder().withName("Mary Tan").build();
        modelManager.addPatient(patient);
        assertTrue(modelManager.hasPerson(patient));

        modelManager.deletePatient(patient);
        assertFalse(modelManager.hasPerson(patient));
    }

    @Test
    public void deletePatient_patientDoesNotExist_throwsNullPointerException() {
        Patient patient = new PatientBuilder().withName("Does Not Exist").build();
        assertThrows(PersonNotFoundException.class, () -> modelManager.deletePatient(patient));
    }

    @Test
    public void setPatient_patientExists_patientUpdatedSuccessfully() {
        Patient patient = new PatientBuilder().withName("Pat One").build();
        modelManager.addPatient(patient);
        assertTrue(modelManager.hasPerson(patient));

        Patient updatedPatient = new PatientBuilder().withName("Pat One Updated").build();
        modelManager.setPatient(patient, updatedPatient);
        assertFalse(modelManager.hasPerson(patient));
        assertTrue(modelManager.hasPerson(updatedPatient));
    }

    @Test
    public void setDoctor_doctorExists_doctorUpdatedSuccessfully() {
        Doctor doctor = new DoctorBuilder().withName("Doc One").build();
        modelManager.addDoctor(doctor);
        assertTrue(modelManager.hasPerson(doctor));

        Doctor updatedDoctor = new DoctorBuilder().withName("Doc One Updated").build();
        modelManager.setDoctor(doctor, updatedDoctor);
        assertFalse(modelManager.hasPerson(doctor));
        assertTrue(modelManager.hasPerson(updatedDoctor));
    }

    @Test
    public void setPerson_personExists_personUpdatedSuccessfully() {
        Person person = new PersonBuilder().withName("Person One").build();
        modelManager.addPerson(person);
        assertTrue(modelManager.hasPerson(person));

        Person updatedPerson = new PatientBuilder().withName("Person One Updated").build();
        modelManager.setPerson(person, updatedPerson);
        assertFalse(modelManager.hasPerson(person));
        assertTrue(modelManager.hasPerson(updatedPerson));
    }

    @Test
    public void getPatientData_returnsCorrectPatientData() {
        Patient patient = new PatientBuilder().withName("Patient Data").build();
        modelManager.addPatient(patient);
        assertEquals(patient, modelManager.getPatientData().getPersonList().get(0));
    }

}
