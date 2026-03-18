package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private Path addressBookFilePath = Paths.get("data" , "addressbook.json");
    private Path patientsFilePath = Paths.get("data", "patients.json");
    private Path doctorsFilePath = Paths.get("data", "doctors.json");
    private Path scheduleFilePath = Paths.get("data", "schedule.json");

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        requireNonNull(newUserPrefs);
        setGuiSettings(newUserPrefs.getGuiSettings());
        setAddressBookFilePath(newUserPrefs.getAddressBookFilePath());
        setPatientsFilePath(newUserPrefs.getPatientsFilePath());
        setDoctorsFilePath(newUserPrefs.getDoctorsFilePath());
        setScheduleFilePath(newUserPrefs.getScheduleFilePath());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    public Path getAddressBookFilePath() {
        return addressBookFilePath;
    }

    public Path getPatientsFilePath() {
        return patientsFilePath;
    }

    public Path getDoctorsFilePath() {
        return doctorsFilePath;
    }

    public Path getScheduleFilePath() {
        return scheduleFilePath;
    }

    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        this.addressBookFilePath = addressBookFilePath;
    }

    public void setPatientsFilePath(Path patientsFilePath) {
        requireNonNull(patientsFilePath);
        this.patientsFilePath = patientsFilePath;
    }

    public void setDoctorsFilePath(Path doctorsFilePath) {
        requireNonNull(doctorsFilePath);
        this.doctorsFilePath = doctorsFilePath;
    }

    public void setScheduleFilePath(Path scheduleFilePath) {
        requireNonNull(scheduleFilePath);
        this.scheduleFilePath = scheduleFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs)) {
            return false;
        }

        UserPrefs otherUserPrefs = (UserPrefs) other;
        return guiSettings.equals(otherUserPrefs.guiSettings)
                && addressBookFilePath.equals(otherUserPrefs.addressBookFilePath)
                && patientsFilePath.equals(otherUserPrefs.patientsFilePath)
                && doctorsFilePath.equals(otherUserPrefs.doctorsFilePath)
                && scheduleFilePath.equals(otherUserPrefs.scheduleFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, addressBookFilePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings);
        sb.append("\nLocal data file location : " + addressBookFilePath);
        sb.append("\nLocal data file location (doctors) : " + doctorsFilePath);
        sb.append("\nLocal data file location (patients): " + patientsFilePath);
        sb.append("\nLocal data file location (schedule): " + scheduleFilePath);
        return sb.toString();
    }

}
