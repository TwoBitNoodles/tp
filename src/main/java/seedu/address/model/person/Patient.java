package seedu.address.model.person;

import java.util.ArrayList;

import seedu.address.model.appointment.Appointment;

/**
 * Represents a patient in the address book.
 * Extends {@code Person} to support the new 'addpat' command.
 */
public class Patient extends Person {
    private ArrayList<Appointment> apptList;

    /**
     * initialises a Patient object
     * @param name
     * @param phone
     * @param email
     * @param address
     */
    public Patient(Name name, Phone phone, Email email, Address address) {
        super(name, phone, email, address);
        this.apptList = new ArrayList<>();
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

    public ArrayList<Appointment> getApptList() {
        return this.apptList;
    }

    public void addAppt(Appointment appt) {
        this.apptList.add(appt);
    }

    public void delAppt(Appointment appt) {
        this.apptList.remove(appt);
    }

    /*@Override
    public String toString() {
      return "Patient: " + super.toString();
    }*/

}
