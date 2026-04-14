package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.storage.AppointmentManager;
import seedu.address.storage.ScheduleManager;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);
    private static final DateTimeFormatter INPUT_TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter STORAGE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final AddressBook addressBook;
    private final AddressBook patients;
    private final AddressBook doctors;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyAddressBook patients,
                        ReadOnlyAddressBook doctors, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, patients, doctors, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.patients = new AddressBook(patients);
        this.doctors = new AddressBook(doctors);
        this.userPrefs = new UserPrefs(userPrefs);

        // Merge patients and doctors into addressBook for UI display
        // Used Copilot for the for loops below to help us get unstuck since our patients and doctors
        // were not being displayed in the app when we ran it, we realised it was a data
        // persistence issue but couldn't figure out the best way to handle it for the MVP
        for (Person p : this.patients.getPersonList()) {
            if (!this.addressBook.hasPerson(p)) {
                this.addressBook.addPerson(p);
            }
        }
        for (Person p : this.doctors.getPersonList()) {
            if (!this.addressBook.hasPerson(p)) {
                this.addressBook.addPerson(p);
            }
        }

        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
    }

    public ModelManager() {
        this(new AddressBook(), new AddressBook(), new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public Path getPatientsFilePath() {
        return userPrefs.getPatientsFilePath();
    }

    @Override
    public Path getDoctorsFilePath() {
        return userPrefs.getDoctorsFilePath();
    }

    @Override
    public Path getScheduleFilePath() {
        return userPrefs.getScheduleFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    @Override
    public void setPatientsFilePath(Path patientsFilePath) {
        requireNonNull(patientsFilePath);
        userPrefs.setPatientsFilePath(patientsFilePath);
    }

    @Override
    public void setDoctorsFilePath(Path doctorsFilePath) {
        requireNonNull(doctorsFilePath);
        userPrefs.setDoctorsFilePath(doctorsFilePath);
    }

    @Override
    public void setScheduleFilePath(Path scheduleFilePath) {
        requireNonNull(scheduleFilePath);
        userPrefs.setScheduleFilePath(scheduleFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public ReadOnlyAddressBook getPatientData() {
        return patients;
    }

    @Override
    public ReadOnlyAddressBook getDoctorData() {
        return doctors;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public boolean hasDoctor(Doctor doctor) {
        requireNonNull(doctor);
        return doctors.hasPerson(doctor);
    }

    @Override
    public boolean hasDoctorExcluding(Doctor doctor, int excludeId) {
        requireNonNull(doctor);
        return doctors.getPersonList().stream()
                .filter(p -> p instanceof Doctor)
                .map(p -> (Doctor) p)
                .filter(d -> d.getDocId() != excludeId)
                .anyMatch(d -> d.isSamePerson(doctor));
    }

    @Override
    public boolean hasPatient(Patient patient) {
        requireNonNull(patient);
        return patients.hasPerson(patient);
    }

    @Override
    public void deleteDoctor(Doctor doctor) {
        doctors.removeDoctor(doctor);
        addressBook.removeDoctor(doctor);
    }

    @Override
    public void deletePatient(Patient patient) {
        patients.removePatient(patient);
        addressBook.removePatient(patient);
        deletePatientByAppt(patient);
    }

    /**
     * Helper function to find a patient and remove all their appointments from the schedule.
     */
    private void deletePatientByAppt(Patient patient) {
        for (Appointment appt : patient.getApptList()) {
            ScheduleManager.removeApptIfExists(appt);
        }
        AppointmentManager.deleteAppointmentsByPatientId(patient.getPatientId());
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void addPatient(Patient patient) {
        patients.addPatient(patient);
        addressBook.addPerson(patient);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void addDoctor(Doctor doctor) {
        doctors.addDoctor(doctor);
        addressBook.addPerson(doctor);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    /**
     * Checks if a doctor with the given name exists in the internal list.
     */
    public boolean hasDoctorWithName(String name) {
        return addressBook.getPersonList().stream()
                .anyMatch(p -> p instanceof Doctor
                        && p.getName().fullName.equalsIgnoreCase(name));
    }

    private Doctor findDoctorById(int docId) {
        return doctors.getPersonList().stream()
                .filter(p -> p instanceof Doctor)
                .map(p -> (Doctor) p)
                .filter(doctor -> doctor.getDocId() == docId)
                .findFirst()
                .orElse(null);
    }

    private Patient findPatientById(int patientId) {
        return patients.getPersonList().stream()
                .filter(p -> p instanceof Patient)
                .map(p -> (Patient) p)
                .filter(patient -> patient.getPatientId() == patientId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addAppt(Appointment appt) throws IOException {
        Patient patient;
        if (appt.getPatientId() != Appointment.UNASSIGNED_ID) {
            patient = findPatientById(appt.getPatientId());
            if (patient == null) {
                throw new IOException("Patient not found: " + appt.getPatientId());
            }
        } else {
            patient = patients.getPersonList().stream()
                    .filter(p -> p instanceof Patient)
                    .map(p -> (Patient) p)
                    .filter(p -> p.getName().fullName.equalsIgnoreCase(appt.getPatName()))
                    .findFirst()
                    .orElseThrow(() -> new IOException("Patient not found: " + appt.getPatName()));
        }
        appt.setPatName(patient.getName().fullName);

        // Check if patient already has appointment at this time
        for (Appointment existing : patient.getApptList()) {
            if (existing.getDate().equals(appt.getDate()) && existing.getTime().equals(appt.getTime())) {
                throw new IOException("Patient already has an appointment at this time");
            }
        }

        Doctor doctor = findDoctorById(appt.getDocId());
        if (doctor == null) {
            throw new IOException("Doctor not found: " + appt.getDocId());
        }
        appt.setDocName(doctor.getName().fullName);

        ScheduleManager.addAppt(appt);
        patient.addAppt(appt);

    }

    @Override
    public void delAppt(Appointment appt) throws IOException {
        Appointment resolvedAppt =
                resolvePatientIdForLegacyAppointment(resolveDoctorIdForLegacyAppointment(appt));

        Patient patient;
        if (resolvedAppt.getPatientId() != Appointment.UNASSIGNED_ID) {
            patient = findPatientById(resolvedAppt.getPatientId());
            if (patient == null) {
                throw new IOException("Patient not found: " + resolvedAppt.getPatientId());
            }
        } else {
            patient = patients.getPersonList().stream()
                    .filter(p -> p instanceof Patient)
                    .map(p -> (Patient) p)
                    .filter(p -> p.getName().fullName.equalsIgnoreCase(resolvedAppt.getPatName()))
                    .findFirst()
                    .orElseThrow(() -> new IOException("Patient not found: " + resolvedAppt.getPatName()));
        }
        if (resolvedAppt.getPatName() == null) {
            resolvedAppt.setPatName(patient.getName().fullName);
        }
        ScheduleManager.delAppt(resolvedAppt);
        patient.delAppt(resolvedAppt);
        if (appt != resolvedAppt) {
            patient.delAppt(appt);
        }
    }

    // method debugged by copilot
    @Override
    public Appointment editAppt(Appointment oldAppt, String newDoc, String newDate, String newTime) throws IOException {
        Appointment resolvedOldAppt =
                resolvePatientIdForLegacyAppointment(resolveDoctorIdForLegacyAppointment(oldAppt));

        int oldDocId = resolvedOldAppt.getDocId();
        String oldDocName = resolvedOldAppt.getDocName();
        String oldDate = resolvedOldAppt.getDate();
        String oldTime = resolvedOldAppt.getTime();
        String oldPatName = resolvedOldAppt.getPatName();
        int oldPatientId = resolvedOldAppt.getPatientId();

        String standardizedOldTime;
        try {
            standardizedOldTime = LocalTime.parse(oldTime, INPUT_TIME_FORMAT).format(STORAGE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IOException("Stored appointment has an invalid time: " + oldTime);
        }

        String scheduledPatName = ScheduleManager.getPatientAtSlotByDocId(oldDocId, oldDate, standardizedOldTime);
        if (scheduledPatName == null) {
            throw new IOException("No appointment exists at: " + oldDocId + " on " + oldDate + " at " + oldTime);
        }

        int finalDocId = oldDocId;
        if (newDoc != null) {
            try {
                finalDocId = Integer.parseInt(newDoc.trim());
            } catch (NumberFormatException e) {
                throw new IOException("Doctor id must be a positive integer.");
            }
            if (finalDocId <= 0) {
                throw new IOException("Doctor id must be a positive integer.");
            }
        }

        Doctor finalDoctor = findDoctorById(finalDocId);
        if (finalDoctor == null) {
            throw new IOException("Doctor not found: " + finalDocId);
        }
        String finalDocName = finalDoctor.getName().fullName;

        String finalDate = (newDate != null) ? newDate : oldDate;
        String finalTime = (newTime != null) ? newTime : oldTime;

        LocalDate parsedFinalDate;
        try {
            parsedFinalDate = LocalDate.parse(finalDate);
        } catch (DateTimeParseException e) {
            throw new IOException("Please input a valid date. The date must be formatted as YYYY-MM-DD");
        }

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        if (parsedFinalDate.isBefore(today) || parsedFinalDate.isAfter(sevenDaysLater)) {
            throw new IOException("Appointment date must be within 7 days from today!");
        }

        LocalTime parsedFinalTime;
        try {
            parsedFinalTime = LocalTime.parse(finalTime, INPUT_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IOException("Please input a valid time. Time must be formatted as H:MM (e.g. 9:00 or 09:00)");
        }

        if (parsedFinalTime.getMinute() % 30 != 0) {
            throw new IOException("Please choose a valid timeslot.");
        }

        String standardizedFinalTime = parsedFinalTime.format(STORAGE_TIME_FORMAT);

        Appointment editedAppt = new Appointment(finalDocId, finalDocName, oldPatientId, oldPatName, finalDate,
                standardizedFinalTime, oldAppt.getApptID());

        // Pre-validate the target slot before deleting the old appointment, to avoid partial edits.
        Map<String, String> targetDaySchedule;
        try {
            targetDaySchedule = ScheduleManager.getScheduleByDocId(finalDocId, finalDate);
        } catch (IllegalArgumentException e) {
            throw new IOException("Date not found! Please choose a date within 7 days of today.");
        }

        if (targetDaySchedule == null) {
            throw new IOException("Doctor not registered");
        }

        if (!targetDaySchedule.containsKey(standardizedFinalTime)) {
            throw new IOException("There is no such time slot.");
        }

        boolean isSameSlot = oldDocId == finalDocId
                && oldDate.equals(finalDate)
                && standardizedOldTime.equals(standardizedFinalTime);
        String targetOccupant = targetDaySchedule.get(standardizedFinalTime);
        if (!isSameSlot && targetOccupant != null) {
            throw new IOException("This slot is already booked. Please edit the appointment if you wish to change it");
        }

        // Also validate that the target time lies within the day's operating hours window.
        TreeMap<String, String> sortedSlots = new TreeMap<>(targetDaySchedule);
        LocalTime first = LocalTime.parse(sortedSlots.firstKey(), STORAGE_TIME_FORMAT);
        LocalTime last = LocalTime.parse(sortedSlots.lastKey(), STORAGE_TIME_FORMAT);
        LocalTime target = LocalTime.parse(standardizedFinalTime, STORAGE_TIME_FORMAT);
        if (target.isBefore(first) || target.isAfter(last)) {
            throw new IOException("Please choose a time within operating hours");
        }

        // Remove both the legacy appointment (if any) and the resolved one.
        deleteApptFromPatient(oldPatientId, oldPatName, oldAppt);
        deleteApptFromPatient(oldPatientId, oldPatName, resolvedOldAppt);
        ScheduleManager.delAppt(resolvedOldAppt);

        try {
            this.addAppt(editedAppt);
        } catch (IOException e) {
            // Best-effort rollback to the old appointment to avoid leaving the schedule inconsistent.
            try {
                this.addAppt(resolvedOldAppt);
            } catch (IOException ignored) {
                // Ignore rollback failure.
            }
            throw e;
        }

        return editedAppt;
    }

    /**
     * For backward-compatibility with older appointments.json entries that did not store a doctor id,
     * attempt to resolve the doctor's id from the stored doctor name.
     */
    private Appointment resolveDoctorIdForLegacyAppointment(Appointment appt) throws IOException {
        if (appt.getDocId() != Appointment.UNASSIGNED_ID) {
            return appt;
        }

        String doctorName = appt.getDocName();
        if (doctorName == null) {
            throw new IOException("Stored appointment is missing doctor id.");
        }

        Doctor doctor = doctors.getPersonList().stream()
                .filter(p -> p instanceof Doctor)
                .map(p -> (Doctor) p)
                .filter(d -> d.getName().fullName.equalsIgnoreCase(doctorName))
                .findFirst()
                .orElse(null);

        if (doctor == null) {
            throw new IOException("Doctor not found: " + doctorName);
        }

        return new Appointment(doctor.getDocId(), doctor.getName().fullName, appt.getPatientId(), appt.getPatName(),
                appt.getDate(), appt.getTime(), appt.getApptID());
    }

    /**
     * For backward-compatibility with older appointments.json entries that did not store a patient id,
     * attempt to resolve the patient's id from the stored patient name.
     */
    private Appointment resolvePatientIdForLegacyAppointment(Appointment appt) throws IOException {
        if (appt.getPatientId() != Appointment.UNASSIGNED_ID) {
            return appt;
        }

        String patientName = appt.getPatName();
        if (patientName == null) {
            throw new IOException("Stored appointment is missing patient id.");
        }

        Patient patient = patients.getPersonList().stream()
                .filter(p -> p instanceof Patient)
                .map(p -> (Patient) p)
                .filter(p -> p.getName().fullName.equalsIgnoreCase(patientName))
                .findFirst()
                .orElse(null);

        if (patient == null) {
            throw new IOException("Patient not found: " + patientName);
        }

        String nameForSchedule = (appt.getPatName() != null) ? appt.getPatName() : patient.getName().fullName;
        return new Appointment(appt.getDocId(), appt.getDocName(), patient.getPatientId(), nameForSchedule,
                appt.getDate(), appt.getTime(), appt.getApptID());
    }

    /**
     * Helper to find a patient and remove an appointment from their internal list.
     */
    private void deleteApptFromPatient(int patientId, String patientName, Appointment appt) {
        Patient patient = null;
        if (patientId != Appointment.UNASSIGNED_ID) {
            patient = findPatientById(patientId);
        }
        if (patient == null && patientName != null) {
            final String targetName = patientName;
            patient = patients.getPersonList().stream()
                    .filter(p -> p instanceof Patient)
                    .map(p -> (Patient) p)
                    .filter(p -> p.getName().fullName.equalsIgnoreCase(targetName))
                    .findFirst()
                    .orElse(null);
        }
        if (patient != null) {
            patient.getApptList().remove(appt);
        }
    }

    /**
     * Helper to check if a patient exists in the master list.
     */
    private boolean hasPatientWithName(String name) {
        return addressBook.getPersonList().stream()
                .anyMatch(p -> p instanceof Patient && p.getName().fullName.equalsIgnoreCase(name));
    }

    /**
     * Updates all appointments for a doctor when the doctor's details change.
     */
    private void updateDoctorAppointmentsInStorage(Doctor oldDoctor, Doctor newDoctor) throws IOException {
        String oldName = oldDoctor.getName().fullName;
        String newName = newDoctor.getName().fullName;

        if (!oldName.equals(newName)) {
            seedu.address.storage.AppointmentManager.updateDoctorNameInAppointments(
                    newDoctor.getDocId(), newName);
            ScheduleManager.renameDoctorSchedule(newDoctor);
        }
    }

    /**
     * Updates all appointments for a patient when the patient's details change.
     */
    private void updatePatientAppointmentsInStorage(Patient oldPatient, Patient newPatient) throws IOException {
        String oldName = oldPatient.getName().fullName;
        String newName = newPatient.getName().fullName;

        if (!oldName.equals(newName)) {
            seedu.address.storage.AppointmentManager.updatePatientNameInAppointments(
                    newPatient.getPatientId(), newName);
        }
    }

    /**
     * Updates all patient name entries in the schedule when the patient's details change.
     */
    private void updatePatientInSchedule(Patient oldPatient, Patient newPatient) throws IOException {
        String oldName = oldPatient.getName().fullName;
        String newName = newPatient.getName().fullName;

        if (!oldName.equals(newName)) {
            ScheduleManager.updatePatientNameInSchedule(oldName, newName);
        }
    }

    /**
     * Updates appointment names in the patient's internal appointment list when name changes.
     * This ensures consistency when later deleting the patient.
     */
    private void updatePatientNameInAppointmentList(Patient oldPatient, Patient newPatient) {
        String oldName = oldPatient.getName().fullName;
        String newName = newPatient.getName().fullName;

        if (!oldName.equals(newName)) {
            for (Appointment appt : newPatient.getApptList()) {
                if (appt.getPatName() != null && appt.getPatName().equalsIgnoreCase(oldName)) {
                    appt.setPatName(newName);
                }
            }
        }
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public void setDoctor(Doctor target, Doctor editedDoctor) {
        requireAllNonNull(target, editedDoctor);

        doctors.setDoctor(target, editedDoctor);
        addressBook.setPerson(target, editedDoctor);

        try {
            updateDoctorAppointmentsInStorage(target, editedDoctor);
        } catch (IOException e) {
            logger.warning("Failed to update doctor appointments in storage: " + e.getMessage());
        }
    }

    @Override
    public void setPatient(Patient target, Patient editedPatient) {
        requireAllNonNull(target, editedPatient);

        patients.setPatient(target, editedPatient);
        addressBook.setPerson(target, editedPatient);

        updatePatientNameInAppointmentList(target, editedPatient);

        try {
            updatePatientAppointmentsInStorage(target, editedPatient);
            updatePatientInSchedule(target, editedPatient);
        } catch (IOException e) {
            logger.warning("Failed to update patient appointments in storage: " + e.getMessage());
        }
    }
    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && patients.equals(otherModelManager.patients)
                && doctors.equals(otherModelManager.doctors)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
