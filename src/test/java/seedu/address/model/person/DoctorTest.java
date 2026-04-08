package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.DoctorBuilder;

public class DoctorTest {

    @Test
    public void constructor_docId_assignsSequentialIds() {
        Doctor.resetIdTracker();
        Doctor alice = new DoctorBuilder().withName("Alice").build();
        Doctor bob = new DoctorBuilder().withName("Bob").build();

        assertEquals(1, alice.getDocId());
        assertEquals(2, bob.getDocId());
    }

    @Test
    public void constructor_providedId_usesProvidedId() {
        Doctor doctor = new DoctorBuilder().withDocId(42).build();
        assertEquals(42, doctor.getDocId());
    }

    @Test
    public void getDocId_returnsAssignedId() {
        Doctor.resetIdTracker();
        Doctor doctor = new DoctorBuilder().build();
        assertEquals(1, doctor.getDocId());
    }

    @Test
    public void getDocIdFromSchedule_providedId_returnsCorrectFormat() {
        Doctor doctor = new DoctorBuilder().withDocId(17).build();
        assertEquals("doc_17", doctor.getDocIdFromSchedule());
    }

    @Test
    public void getDocIdFromSchedule_id_returnsCorrectFormat() {
        Doctor.resetIdTracker();
        Doctor doctor = new DoctorBuilder().build();
        assertEquals("doc_1", doctor.getDocIdFromSchedule());
    }

    @Test
    public void resetIdTracker_resetsTracker() {
        new DoctorBuilder().withName("Alice").build();
        new DoctorBuilder().withName("Bob").build();

        Doctor.resetIdTracker();
        Doctor doctor = new DoctorBuilder().build();
        assertEquals(1, doctor.getDocId());
    }

    @Test
    public void setIdTracker_setsNextId() {
        Doctor.setIdTracker(10);
        Doctor doctor = new DoctorBuilder().build();
        assertEquals(10, doctor.getDocId());
    }

    @Test
    public void setIdTracker_newDoctorAssignedCorrectId() {
        Doctor.setIdTracker(5);
        Doctor alice = new DoctorBuilder().withName("Alice").build();
        Doctor bob = new DoctorBuilder().withName("Bob").build();
        assertEquals(5, alice.getDocId());
        assertEquals(6, bob.getDocId());
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Doctor alice = new DoctorBuilder().build();
        assertTrue(alice.equals(alice));
    }

    @Test
    public void equals_null_returnsFalse() {
        Doctor alice = new DoctorBuilder().build();
        assertFalse(alice.equals(null));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        Doctor alice = new DoctorBuilder().withDocId(1).withName("Alice")
                .withPhone("12121212").withEmail("alice@wonderland.com")
                .withAddress("12 Rabbit").build();
        Doctor aliceClone = new DoctorBuilder().withDocId(1).withName("Alice")
                .withPhone("12121212").withEmail("alice@wonderland.com")
                .withAddress("12 Rabbit").build();
        assertTrue(alice.equals(aliceClone));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Doctor alice = new DoctorBuilder().build();
        assertFalse(alice.equals(5));
    }

    @Test
    public void equals_differentName_returnsFalse() {
        Doctor alice = new DoctorBuilder().withName("Alice").build();
        Doctor bob = new DoctorBuilder().withName("Bob").build();
        assertFalse(alice.equals(bob));
    }

    @Test
    public void equals_differentEmail_returnsFalse() {
        Doctor alice = new DoctorBuilder().withEmail("alice@wonderland.com").build();
        Doctor other = new DoctorBuilder().withEmail("bob@minions.com").build();
        assertFalse(alice.equals(other));
    }

    @Test
    public void equals_differentAddress_returnsFalse() {
        Doctor alice = new DoctorBuilder().withAddress("12 Rabbit").build();
        Doctor other = new DoctorBuilder().withAddress("13 Hole").build();
        assertFalse(alice.equals(other));
    }

    @Test
    public void equals_differentPhone_returnsFalse() {
        Doctor alice = new DoctorBuilder().withPhone("12121212").build();
        Doctor other = new DoctorBuilder().withPhone("89898989").build();
        assertFalse(alice.equals(other));
    }

    @Test
    public void equals_sameFieldsDifferentDocId_returnsTrue() {
        Doctor alice = new DoctorBuilder().withDocId(1).withName("Alice")
                .withPhone("12121212").withEmail("alice@wonderland.com").withAddress("Street").build();
        Doctor bob = new DoctorBuilder().withDocId(99).withName("Alice")
                .withPhone("12121212").withEmail("alice@wonderland.com").withAddress("Street").build();
        assertTrue(alice.equals(bob));
    }

    @Test
    public void isSamePerson_null_returnsFalse() {
        Doctor alice = new DoctorBuilder().build();
        assertFalse(alice.isSamePerson(null));
    }

    @Test
    public void isSamePerson_sameObject_returnsTrue() {
        Doctor alice = new DoctorBuilder().build();
        assertTrue(alice.isSamePerson(alice));
    }

    @Test
    public void isSamePerson_differentName_returnsFalse() {
        Doctor alice = new DoctorBuilder().withName("Alice").withPhone("12121212")
                .withEmail("alice@wonderland.com").build();
        Doctor other = new DoctorBuilder().withName("Bob").withPhone("12121212")
                .withEmail("alice@wonderland.com").build();
        assertFalse(alice.isSamePerson(other));
    }

    @Test
    public void isSamePerson_nonDoctorInstance_returnsFalse() {
        Doctor alice = new DoctorBuilder().withName("Alice").withPhone("12121212")
                .withEmail("alice@wonderland.com").withAddress("12 Rabbit").build();
        Person aliceClone = new Person(new Name("Alice"), new Phone("12121212"),
                new Email("alice@wonderland.com"), new Address("12 Rabbit"));
        assertFalse(alice.isSamePerson(aliceClone));
    }

    @Test
    public void isSamePerson_sameNameAndPhone_returnsTrue() {
        Doctor alice = new DoctorBuilder().withName("Alice").withPhone("12121212")
                .withEmail("alice@wonderland.com").build();
        Doctor other = new DoctorBuilder().withName("Alice").withPhone("12121212")
                .withEmail("notalice@wonderland.com").build();
        assertTrue(alice.isSamePerson(other));
    }

    @Test
    public void isSamePerson_sameNameAndEmail_returnsTrue() {
        Doctor alice = new DoctorBuilder().withName("Alice").withPhone("12121212")
                .withEmail("alice@wonderland.com").build();
        Doctor other = new DoctorBuilder().withName("Alice").withPhone("99999999")
                .withEmail("alice@wonderland.com").build();
        assertTrue(alice.isSamePerson(other));
    }

    @Test
    public void isSamePerson_sameNameDifferentCase_returnsTrue() {
        Doctor alice = new DoctorBuilder().withName("Alice").withPhone("12121212")
                .withEmail("alice@wonderland.com").build();
        Doctor other = new DoctorBuilder().withName("ALICE").withPhone("12121212")
                .withEmail("notalice@wonderland.com").build();
        assertTrue(alice.isSamePerson(other));
    }

    @Test
    public void isSamePerson_sameNameDifferentPhoneEmail_returnsFalse() {
        Doctor alice = new DoctorBuilder().withName("Alice").withPhone("12121212")
                .withEmail("alice@wonderland.com").build();
        Doctor other = new DoctorBuilder().withName("Alice").withPhone("99999999")
                .withEmail("notalice@wonderland.com").build();
        assertFalse(alice.isSamePerson(other));
    }

    @Test
    public void docIdFromSchedule_differentDoctors_differentKeys() {
        Doctor.resetIdTracker();
        Doctor alice = new DoctorBuilder().withName("Alice").build();
        Doctor bob = new DoctorBuilder().withName("Bob").build();
        assertNotEquals(alice.getDocIdFromSchedule(), bob.getDocIdFromSchedule());
    }

    @Test
    public void docIdFromSchedule_sameNameDoctors_differentKeys() {
        Doctor.resetIdTracker();
        Doctor alice = new DoctorBuilder().withName("Alice").withPhone("11111111")
                .withEmail("alice@wonderland.com").build();
        Doctor bob = new DoctorBuilder().withName("Alice").withPhone("22222222")
                .withEmail("bob@minions.com").build();
        assertNotEquals(alice.getDocIdFromSchedule(), bob.getDocIdFromSchedule());
    }

    @Test
    public void hashCode_sameFieldsDifferentDocId_sameHashCode() {
        Doctor alice = new DoctorBuilder().withDocId(1).withName("Alice")
                .withPhone("12121212").withEmail("alice@wonderland.com").withAddress("Street").build();
        Doctor bob = new DoctorBuilder().withDocId(99).withName("Alice")
                .withPhone("12121212").withEmail("alice@wonderland.com").withAddress("Street").build();
        assertEquals(alice.hashCode(), bob.hashCode());
    }

    @Test
    public void hashCode_differentFields_differentHashCode() {
        Doctor alice = new DoctorBuilder().withName("Alice").build();
        Doctor bob = new DoctorBuilder().withName("Bob").build();
        assertNotEquals(alice.hashCode(), bob.hashCode());
    }
}
// several test case NAMES are inspired by copilot, since we wanted more standardisation
