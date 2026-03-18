package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage extends AddressBookStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getAddressBookFilePath();

    Path getPatientsFilePath();

    Path getDoctorsFilePath();

    Path getScheduleFilePath();

    @Override
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException;

    Optional<ReadOnlyAddressBook> readPatientData() throws DataLoadingException;

    Optional<ReadOnlyAddressBook> readDoctorData() throws DataLoadingException;

    Optional<ReadOnlyAddressBook> readScheduleData() throws DataLoadingException;

    @Override
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

    void savePatientData(ReadOnlyAddressBook patientData) throws IOException;

    void saveDoctorData(ReadOnlyAddressBook doctorData) throws IOException;

    void saveScheduleData(ReadOnlyAddressBook scheduleData) throws IOException;

}
