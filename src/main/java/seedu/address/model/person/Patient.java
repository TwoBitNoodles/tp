package seedu.address.model.person;

import java.util.ArrayList;

import seedu.address.model.appointment.Appointment;

/**
 * Represents a patient in the address book.
 * Extends {@code Person} to support the new 'addpat' command.
 */
public class Patient extends Person {
    private static final String SCHEDULE_KEY_PREFIX = "pat_";

    private static int nextId = 1;

    private final int patientId;
    private ArrayList<Appointment> apptList;

    /**
     * Creates a Patient with an auto-generated ID.
     * @param name
     * @param phone
     * @param email
     * @param address
     */
    public Patient(Name name, Phone phone, Email email, Address address) {
        super(name, phone, email, address);
        this.patientId = nextId++;
        this.apptList = new ArrayList<>();
    }

    /**
     * Creates a Patient with a specific ID.
     * @param name
     * @param phone
     * @param email
     * @param address
     * @param patientId
     */
    public Patient(Name name, Phone phone, Email email, Address address, int patientId) {
        super(name, phone, email, address);
        this.patientId = patientId;
        this.apptList = new ArrayList<>();
    }

    @Override
    public String getRoleTag() {
        return "Patient";
    }

    @Override
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        if (!(otherPerson instanceof Patient)) {
            return false;
        }

        return otherPerson != null
            && otherPerson.getName().fullName.equalsIgnoreCase(getName().fullName)
            && otherPerson.getEmail().equals(getEmail());
    }

    public int getPatientId() {
        return patientId;
    }

    /**
     * Sets the starting value for the patient ID counter.
     */
    public static void setIdTracker(int nextIdValue) {
        nextId = nextIdValue;
    }

    /**
     * Returns the key to access the relevant patient within schedule.json.
     */
    public String getPatientIdFromSchedule() {
        return SCHEDULE_KEY_PREFIX + patientId;
    }

    /**
     * Resets the ID tracker.
     */
    public static void resetIdTracker() {
        nextId = 1;
    }

    public ArrayList<Appointment> getApptList() {
        return this.apptList;
    }

    public void addAppt(Appointment appt) {
        this.apptList.add(appt);
    }

    public void delAppt(Appointment appt) {
        this.apptList.remove(appt);
    }

}
