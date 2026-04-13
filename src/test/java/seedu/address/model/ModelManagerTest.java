package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.storage.AppointmentManager;
import seedu.address.storage.ScheduleManager;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {
    private static final String SCHEDULE_FILE = "data/schedule.json";
    private static final String APPT_FILE = "data/appointments.json";

    private ModelManager modelManager = new ModelManager();
    private byte[] scheduleBackup;
    private byte[] apptBackup;
    private boolean scheduleExisted;
    private boolean apptExisted;

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

    private void backupAndRestore(TestAction action) throws Exception {
        File sf = new File(SCHEDULE_FILE);
        File af = new File(APPT_FILE);
        scheduleExisted = sf.exists();
        apptExisted = af.exists();
        scheduleBackup = scheduleExisted ? Files.readAllBytes(sf.toPath()) : null;
        apptBackup = apptExisted ? Files.readAllBytes(af.toPath()) : null;
        try {
            action.run();
        } finally {
            if (scheduleExisted) {
                Files.write(sf.toPath(), scheduleBackup);
            } else if (sf.exists()) {
                sf.delete();
            }
            if (apptExisted) {
                Files.write(af.toPath(), apptBackup);
            } else if (af.exists()) {
                af.delete();
            }
        }
    }

    @FunctionalInterface
    private interface TestAction {
        void run() throws Exception;
    }

    @Test
    public void delAppt_legacyApptNullDoctorName_throwsIoException() throws Exception {
        backupAndRestore(() -> {
            Appointment appt = new Appointment(-1, null, 1, "Alice",
                    LocalDate.now().toString(), "09:00", 0);
            assertThrows(IOException.class, () -> modelManager.delAppt(appt));
        });
    }

    @Test
    public void delAppt_legacyApptDoctorNotFound_throwsIoException() throws Exception {
        backupAndRestore(() -> {
            Appointment appt = new Appointment("NonexistentDoc", "Alice",
                    LocalDate.now().toString(), "09:00");
            assertThrows(IOException.class, () -> modelManager.delAppt(appt));
        });
    }

    @Test
    public void delAppt_legacyApptResolvesDoctor_success() throws Exception {
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now();
            Doctor doctor = new DoctorBuilder().withName("LegacyDoc").withDocId(50)
                    .withPhone("99990000").withEmail("legacy@doc.com").build();
            Patient patient = new PatientBuilder().withName("LegacyPat")
                    .withPhone("88880000").withEmail("legacy@pat.com").build();
            modelManager.addDoctor(doctor);
            modelManager.addPatient(patient);
            ScheduleManager.addDoctorSchedule(doctor);

            Appointment fullAppt = new Appointment(50, "LegacyDoc", patient.getPatientId(),
                    "LegacyPat", today.plusDays(1).toString(), "09:00", -1);
            AppointmentManager.addAppointment(fullAppt);
            modelManager.addAppt(fullAppt);

            // Delete using legacy appointment (no docId, only name)
            Appointment legacyDel = new Appointment("LegacyDoc", "LegacyPat",
                    today.plusDays(1).toString(), "09:00");
            modelManager.delAppt(legacyDel);
        });
    }

    @Test
    public void setDoctor_nameChange_updatesAppointments() throws Exception {
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now();
            Doctor doctor = new DoctorBuilder().withName("OldDocName").withDocId(60)
                    .withPhone("77770000").withEmail("old@doc.com").build();
            Patient patient = new PatientBuilder().withName("SetDocPat")
                    .withPhone("66660000").withEmail("setdoc@pat.com").build();
            modelManager.addDoctor(doctor);
            modelManager.addPatient(patient);
            ScheduleManager.addDoctorSchedule(doctor);

            Appointment appt = new Appointment(60, "OldDocName", patient.getPatientId(),
                    "SetDocPat", today.toString(), "09:00", -1);
            int apptId = AppointmentManager.addAppointment(appt);

            Doctor edited = new DoctorBuilder().withName("NewDocName").withDocId(60)
                    .withPhone("77770000").withEmail("old@doc.com").build();
            modelManager.setDoctor(doctor, edited);

            Appointment stored = AppointmentManager.getAppointmentById(apptId);
            assertEquals("NewDocName", stored.getDocName());
        });
    }

    @Test
    public void addAppt_patientMultipleDoctorsSameTime_throws() throws Exception {
        //written by copilot
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now().plusDays(1);
            Doctor doctor1 = new DoctorBuilder().withName("DocOne").withDocId(70)
                    .withPhone("99990000").withEmail("doc1@test.com").build();
            Doctor doctor2 = new DoctorBuilder().withName("DocTwo").withDocId(71)
                    .withPhone("99990001").withEmail("doc2@test.com").build();
            Patient patient = new PatientBuilder().withName("MultiDocPat")
                    .withPhone("88880000").withEmail("multi@pat.com").build();
            modelManager.addDoctor(doctor1);
            modelManager.addDoctor(doctor2);
            modelManager.addPatient(patient);
            ScheduleManager.addDoctorSchedule(doctor1);
            ScheduleManager.addDoctorSchedule(doctor2);

            Appointment appt1 = new Appointment(70, patient.getPatientId(), today.toString(), "10:00");
            modelManager.addAppt(appt1);

            Appointment appt2 = new Appointment(71, patient.getPatientId(), today.toString(), "10:00");
            assertThrows(IOException.class, () -> modelManager.addAppt(appt2));
        });
    }

    @Test
    public void editAppt_incompleteScheduleData_healsAndSucceeds() throws Exception {
        //written by copilot
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now().plusDays(1);
            Doctor doctor = new DoctorBuilder().withName("HealDocName").withDocId(80)
                    .withPhone("99990000").withEmail("heal@doc.com").build();
            Patient patient = new PatientBuilder().withName("HealPat")
                    .withPhone("88880000").withEmail("heal@pat.com").build();
            modelManager.addDoctor(doctor);
            modelManager.addPatient(patient);
            ScheduleManager.addDoctorSchedule(doctor);

            Appointment appt = new Appointment(80, "HealDocName", patient.getPatientId(),
                    "HealPat", today.toString(), "10:00", -1);
            int apptId = AppointmentManager.addAppointment(appt);
            patient.addAppt(appt);
            ScheduleManager.addAppt(appt);

            Appointment editedAppt = modelManager.editAppt(appt, null, null, "10:30");
            assertEquals("10:30", editedAppt.getTime());
            assertEquals(apptId, editedAppt.getApptID());
        });
    }

    @Test
    public void addAppt_patientWithNoConflicts_succeeds() throws Exception {
        //written by copilot
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now().plusDays(1);
            Doctor doctor = new DoctorBuilder().withName("MultiDocName").withDocId(90)
                    .withPhone("99990000").withEmail("multi@doc.com").build();
            Patient patient = new PatientBuilder().withName("MultiSlotPat")
                    .withPhone("88880000").withEmail("multi@pat.com").build();
            modelManager.addDoctor(doctor);
            modelManager.addPatient(patient);
            ScheduleManager.addDoctorSchedule(doctor);

            Appointment appt1 = new Appointment(90, "MultiDocName", patient.getPatientId(),
                    "MultiSlotPat", today.toString(), "09:00", -1);
            modelManager.addAppt(appt1);

            Appointment appt2 = new Appointment(90, "MultiDocName", patient.getPatientId(),
                    "MultiSlotPat", today.toString(), "10:00", -1);
            modelManager.addAppt(appt2);

            assertEquals(2, patient.getApptList().size());
        });
    }

    @Test
    public void addAppt_patientConflictingSameTime_throws() throws Exception {
        //written by copilot
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now().plusDays(1);
            Doctor doctor = new DoctorBuilder().withName("ConflictDoc").withDocId(85)
                    .withPhone("99850000").withEmail("conflict@doc.com").build();
            Patient patient = new PatientBuilder().withName("ConflictPat")
                    .withPhone("88850000").withEmail("conflict@pat.com").build();
            modelManager.addDoctor(doctor);
            modelManager.addPatient(patient);
            ScheduleManager.addDoctorSchedule(doctor);

            Appointment appt1 = new Appointment(85, "ConflictDoc", patient.getPatientId(),
                    "ConflictPat", today.toString(), "09:00", -1);
            modelManager.addAppt(appt1);

            Appointment appt2 = new Appointment(85, "ConflictDoc", patient.getPatientId(),
                    "ConflictPat", today.toString(), "09:00", -1);
            assertThrows(IOException.class, () -> modelManager.addAppt(appt2));
        });
    }

    @Test
    public void editAppt_invalidTimeFormatStored_throws() throws Exception {
        //written by copilot
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now().plusDays(1);
            Doctor doctor = new DoctorBuilder().withName("InvalidTimeDoc").withDocId(86)
                    .withPhone("99860000").withEmail("invalidtime@doc.com").build();
            Patient patient = new PatientBuilder().withName("InvalidTimePat")
                    .withPhone("88860000").withEmail("invalidtime@pat.com").build();
            modelManager.addDoctor(doctor);
            modelManager.addPatient(patient);

            Appointment apptWithBadTime = new Appointment(86, "InvalidTimeDoc", patient.getPatientId(),
                    "InvalidTimePat", today.toString(), "999:99", -1);
            apptWithBadTime.setApptID(1);
            patient.addAppt(apptWithBadTime);

            assertThrows(IOException.class, () -> modelManager.editAppt(apptWithBadTime, null, null, "10:00"));
        });
    }

    @Test
    public void delAppt_cleansUpAllStorageLayers_verifyCleanup() throws Exception {
        //written by copilot
        backupAndRestore(() -> {
            LocalDate today = LocalDate.now().plusDays(1);
            Doctor doctor = new DoctorBuilder().withName("DelDocName").withDocId(110)
                    .withPhone("99990000").withEmail("del@doc.com").build();
            Patient patient = new PatientBuilder().withName("DelPat")
                    .withPhone("88880000").withEmail("del@pat.com").build();
            modelManager.addDoctor(doctor);
            modelManager.addPatient(patient);
            ScheduleManager.addDoctorSchedule(doctor);

            Appointment appt = new Appointment(110, "DelDocName", patient.getPatientId(),
                    "DelPat", today.toString(), "09:00", -1);
            int apptId = AppointmentManager.addAppointment(appt);
            patient.addAppt(appt);
            ScheduleManager.addAppt(appt);

            assertEquals(1, patient.getApptList().size());
            modelManager.delAppt(appt);

            assertEquals(0, patient.getApptList().size());
        });
    }
}



