package seedu.address.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Returns the user prefs' patients file path.
     */
    Path getPatientsFilePath();

    /**
     * Returns the user prefs' doctors file path.
     */
    Path getDoctorsFilePath();

    /**
     * Returns the user prefs' schedule file path.
     */
    Path getScheduleFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Sets the user prefs' patients file path.
     */
    void setPatientsFilePath(Path patientsFilePath);

    /**
     * Sets the user prefs' doctors file path.
     */
    void setDoctorsFilePath(Path doctorsFilePath);

    /**
     * Sets the user prefs' schedule file path.
     */
    void setScheduleFilePath(Path scheduleFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /** Returns the PatientData */
    ReadOnlyAddressBook getPatientData();

    /** Returns the DoctorData */
    ReadOnlyAddressBook getDoctorData();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns true if a doctor with the same identity as {@code doctor} exists in the doctors list.
     */
    boolean hasDoctor(Doctor doctor);

    /**
     * Deletes the given patient.
     * The patient must exist in the address book.
     */
    void deletePatient(Patient patient);

    /**
     * Deletes the given doctor.
     * The doctor must exist in the address book.
     */
    void deleteDoctor(Doctor doctor);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Adds the given appointment
     * @param appt
     */
    void addAppt(Appointment appt) throws IOException;

    /**
     * deletes the given appointment
     * @param appt
     */
    void delAppt(Appointment appt) throws IOException;

    /**
     * edits the given appointment to alter the info
     * @param oldDoc
     * @param oldDate
     * @param oldTime
     * @param newPat
     * @param newDoc
     * @param newDate
     * @param newTime
     */
    void editAppt(String oldDoc, String oldDate,
                  String oldTime, String newPat, String newDoc, String newDate, String newTime) throws IOException;

    /**
     * Adds the given doctor.
     * {@code doctor} must not already exist in the app.
     */
    void addDoctor(Doctor doctor);

    /**
     * Adds the given patient.
     * {@code patient} must not already exist in the app.
     */
    void addPatient(Patient patient);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Replaces the given doctor {@code target} with {@code editedDoctor}.
     * {@code target} must exist in the app.
     * The person identity of {@code editedDoctor} must not be the same as another existing doctor in the app.
     */
    void setDoctor(Doctor target, Doctor editedDoctor);

    /**
     * Replaces the given patient {@code target} with {@code editedPatient}.
     * {@code target} must exist in the app.
     * The person identity of {@code editedPatient} must not be the same as another existing patient in the app.
     */
    void setPatient(Patient target, Patient editedPatient);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);


}
