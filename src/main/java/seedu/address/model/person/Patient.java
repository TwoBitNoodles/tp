package seedu.address.model.person;

import java.util.ArrayList;
import java.util.Set;

import seedu.address.model.appointment.Appointment;
import seedu.address.model.tag.Tag;

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
     * @param tags
     */
    public Patient(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        super(name, phone, email, address, tags);
        this.apptList = new ArrayList<>();
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
