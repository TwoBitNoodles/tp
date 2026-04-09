package seedu.address.model.person;

/**
 * Represents a doctor in the app.
 * Extends {@code Person} to support the new 'adddoc' command.
 */
public class Doctor extends Person {

    private static final String SCHEDULE_KEY_PREFIX = "doc_";

    private static int nextId = 1;

    private final int docId;

    /**
     * Creates a Doctor with an auto-generated ID.
     */
    public Doctor(Name name, Phone phone, Email email, Address address) {
        super(name, phone, email, address);
        this.docId = nextId++;
    }

    /**
     * Creates a Doctor with a specific ID.
     */
    public Doctor(Name name, Phone phone, Email email, Address address, int docId) {
        super(name, phone, email, address);
        this.docId = docId;
    }

    public int getDocId() {
        return docId;
    }

    @Override
    public String getRoleTag() {
        return "Doctor";
    }

    /**
     * Sets the starting value for the doctor ID counter.
     */
    public static void setIdTracker(int nextIdValue) {
        nextId = nextIdValue;
    }

    /**
     * Returns the key to access the relevant doctor with in schedule.json.
     */
    public String getDocIdFromSchedule() {
        return SCHEDULE_KEY_PREFIX + docId;
    }

    /**
     * Resets the ID tracker.
     */
    public static void resetIdTracker() {
        nextId = 1;
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

        return otherPerson.getName().fullName.equalsIgnoreCase(getName().fullName)
                && (otherPerson.getPhone().equals(getPhone())
                    || otherPerson.getEmail().equals(getEmail()));
    }
}
