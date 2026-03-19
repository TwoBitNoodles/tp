package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalDoctors.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.PatientBuilder;

public class DeletePatCommandTest {
    @Test
    public void execute_validIndex_patientDeleted() throws CommandException {
        Model model = new ModelManager();
        Patient alice = new PatientBuilder().withName("Alice")
                .withPhone("98765432")
                .withEmail("alice@example.com")
                .withAddress("123 Street")
                .build();

        Patient bob = new PatientBuilder().withName("Bob")
                .withPhone("91234567")
                .withEmail("bob@example.com")
                .withAddress("456 Street")
                .build();

        model.addPatient(alice);
        model.addPatient(bob);

        DeletePatCommand deletePatCommand = new DeletePatCommand(Index.fromOneBased(1));

        CommandResult result = deletePatCommand.execute(model);
        // Check success message
        assertEquals(String.format(DeletePatCommand.MESSAGE_DELETE_PATIENT_SUCCESS,
                Messages.format(alice)), result.getFeedbackToUser());

        // Check that Alice is removed
        assertFalse(model.getFilteredPersonList().contains(alice));

        // Bob still exists
        assertTrue(model.getFilteredPersonList().contains(bob));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager();
        Patient alice = new PatientBuilder().withName("Alice")
                .withPhone("98765432")
                .withEmail("alice@example.com")
                .withAddress("123 Street")
                .build();

        Patient bob = new PatientBuilder().withName("Bob")
                .withPhone("91234567")
                .withEmail("bob@example.com")
                .withAddress("456 Street")
                .build();

        model.addPatient(alice);
        model.addPatient(bob);

        DeletePatCommand deletePatCommand = new DeletePatCommand(Index.fromOneBased(3));

        CommandException thrown = assertThrows(CommandException.class, () ->
                deletePatCommand.execute(model));

        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, thrown.getMessage());
    }

    @Test
    public void equals() {
        DeletePatCommand deleteFirstCommand = new DeletePatCommand(INDEX_FIRST_PERSON);
        DeletePatCommand deleteSecondCommand = new DeletePatCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeletePatCommand deleteFirstCommandCopy = new DeletePatCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void execute_nonPatientIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), getTypicalAddressBook(),
                getTypicalAddressBook(), new UserPrefs());
        Patient alice = new PatientBuilder().withName("Alice")
                .withPhone("98765432")
                .withEmail("alice@example.com")
                .withAddress("123 Street")
                .build();

        Doctor smith = new DoctorBuilder().withName("Smith")
                .withPhone("91234567")
                .withEmail("smith@clinic.com")
                .withAddress("789 Street")
                .build();

        model.addPatient(alice);
        model.addDoctor(smith);
        DeletePatCommand deletePatCommand = new DeletePatCommand(Index.fromOneBased(2));
        CommandException thrown = assertThrows(CommandException.class, () ->
                deletePatCommand.execute(model));
        assertEquals("The person at the specified index is not a patient.", thrown.getMessage());

    }



}
