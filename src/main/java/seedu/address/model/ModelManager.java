package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
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
import seedu.address.storage.ScheduleManager;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

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
    public void deleteDoctor(Doctor doctor) {
        doctors.removeDoctor(doctor);
        addressBook.removeDoctor(doctor);
    }

    @Override
    public void deletePatient(Patient patient) {
        patients.removePatient(patient);
        addressBook.removePatient(patient);
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

    @Override
    public void addAppt(Appointment appt) {
        ScheduleManager.addAppt(appt);

    }

    @Override
    public void delAppt(Appointment appt) {
        ScheduleManager.delAppt(appt);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
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
