package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private AddressBookStorage patientDataStorage;
    private AddressBookStorage doctorDataStorage;
    private AddressBookStorage scheduleDataStorage;
    private UserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code AddressBookStorage} and {@code UserPrefStorage}.
     */
    public StorageManager(AddressBookStorage patientDataStorage,
                          AddressBookStorage doctorDataStorage,
                          AddressBookStorage scheduleDataStorage,
                          UserPrefsStorage userPrefsStorage) {
        // this.addressBookStorage = addressBookStorage;
        this.patientDataStorage = patientDataStorage;
        this.doctorDataStorage = doctorDataStorage;
        this.scheduleDataStorage = scheduleDataStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Path getPatientsFilePath() {
        return patientDataStorage.getAddressBookFilePath();
    }

    @Override
    public Path getDoctorsFilePath() {
        return doctorDataStorage.getAddressBookFilePath();
    }

    @Override
    public Path getScheduleFilePath() {
        return scheduleDataStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public Optional<ReadOnlyAddressBook> readPatientData() throws DataLoadingException {
        return readAddressBook(patientDataStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readDoctorData() throws DataLoadingException {
        return readAddressBook(doctorDataStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readScheduleData() throws DataLoadingException {
        return readAddressBook(scheduleDataStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    @Override
    public void savePatientData(ReadOnlyAddressBook patientData) throws IOException {
        saveAddressBook(patientData, patientDataStorage.getAddressBookFilePath());
    }

    @Override
    public void saveDoctorData(ReadOnlyAddressBook doctorData) throws IOException {
        saveAddressBook(doctorData, doctorDataStorage.getAddressBookFilePath());
    }

    @Override
    public void saveScheduleData(ReadOnlyAddressBook scheduleData) throws IOException {
        saveAddressBook(scheduleData, scheduleDataStorage.getAddressBookFilePath());
    }

}
