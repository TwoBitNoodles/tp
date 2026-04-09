package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.ScheduleInitialiser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 2, 2, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing AddressBook ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        AddressBookStorage addressBookStorage = new JsonAddressBookStorage(userPrefs.getAddressBookFilePath());
        AddressBookStorage patientDataStorage = new JsonAddressBookStorage(userPrefs.getPatientsFilePath());
        AddressBookStorage doctorDataStorage = new JsonAddressBookStorage(userPrefs.getDoctorsFilePath());
        AddressBookStorage scheduleDataStorage = new JsonAddressBookStorage(userPrefs.getScheduleFilePath());
        storage = new StorageManager(patientDataStorage, doctorDataStorage,
                scheduleDataStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);
        ScheduleInitialiser.initialize(model);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info(String.format("Using data files : %s, %s, %s",
                storage.getPatientsFilePath(),
                storage.getDoctorsFilePath(),
                storage.getScheduleFilePath()));

        ReadOnlyAddressBook initialData = new AddressBook();
        ReadOnlyAddressBook patientData = loadPatientData(storage);
        ReadOnlyAddressBook doctorData = loadDoctorData(storage);

        initializePatientIdTracker(patientData);
        initializeDoctorIdTracker(doctorData);

        return new ModelManager(initialData, patientData, doctorData, userPrefs);
    }

    /**
     * Loads patient data from storage, returning an empty AddressBook if the file is missing or corrupted.
     */
    private ReadOnlyAddressBook loadPatientData(Storage storage) {
        try {
            Optional<ReadOnlyAddressBook> patientDataOptional = storage.readPatientData();
            if (!patientDataOptional.isPresent()) {
                logger.info("Patient data file not found. Starting with empty patient data.");
            }
            return patientDataOptional.orElse(new AddressBook());
        } catch (DataLoadingException e) {
            logger.warning("Patient data could not be loaded. Starting with empty patient data.");
            return new AddressBook();
        }
    }

    /**
     * Loads doctor data from storage, returning an empty AddressBook if the file is missing or corrupted.
     */
    private ReadOnlyAddressBook loadDoctorData(Storage storage) {
        try {
            Optional<ReadOnlyAddressBook> doctorDataOptional = storage.readDoctorData();
            if (!doctorDataOptional.isPresent()) {
                logger.info("Doctor data file not found. Starting with empty doctor data.");
            }
            return doctorDataOptional.orElse(new AddressBook());
        } catch (DataLoadingException e) {
            logger.warning("Doctor data could not be loaded. Starting with empty doctor data.");
            return new AddressBook();
        }
    }

    /**
     * Sets the Doctor ID tracker to one past the highest existing doctor ID.
     */
    private void initializeDoctorIdTracker(ReadOnlyAddressBook doctorData) {
        int maxId = 0;
        for (Person p : doctorData.getPersonList()) {
            if (p instanceof Doctor && ((Doctor) p).getDocId() > maxId) {
                maxId = ((Doctor) p).getDocId();
            }
        }
        Doctor.setIdTracker(maxId + 1);
    }

    /**
     * Sets the Patient ID tracker to one past the highest existing patient ID.
     */
    private void initializePatientIdTracker(ReadOnlyAddressBook patientData) {
        int maxId = 0;
        for (Person p : patientData.getPersonList()) {
            if (p instanceof Patient && ((Patient) p).getPatientId() > maxId) {
                maxId = ((Patient) p).getPatientId();
            }
        }
        Patient.setIdTracker(maxId + 1);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting AddressBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping AddressBook ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
