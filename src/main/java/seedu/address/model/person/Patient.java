package seedu.address.model.person;

import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a patient in the address book.
 * Extends {@code Person} to support the new 'addpat' command.
 */
public class Patient extends Person {

    public Patient(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        super(name, phone, email, address, tags);
    }

    @Override
    public String toString() {
        return "Patient: " + super.toString();
    }

}
