package seedu.address.model.person;

/**
 * Represents a doctor in the app.
 * Extends {@code Person} to support the new 'adddoc' command.
 */
public class Doctor extends Person {
    public Doctor(Name name, Phone phone, Email email, Address address) {
        super(name, phone, email, address);
    }

    /**
     * Returns true if both doctors have the same name (case-insensitive)
     * and share the same phone number or email.
     */
    @Override
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        if (!(otherPerson instanceof Doctor)) {
            return false;
        }

        return otherPerson != null
                && otherPerson.getName().fullName.equalsIgnoreCase(getName().fullName)
                && (otherPerson.getPhone().equals(getPhone())
                    || otherPerson.getEmail().equals(getEmail()));
    }
}
