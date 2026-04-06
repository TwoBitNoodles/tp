package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalDoctors.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.testutil.PatientBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteDocCommand}.
 */
public class DeleteDocCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalAddressBook(),
            getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteDocCommand deleteDocCommand = new DeleteDocCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteDocCommand.MESSAGE_DELETE_DOCTOR_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), model.getPatientData(),
                model.getDoctorData(), new UserPrefs());
        expectedModel.deleteDoctor((Doctor) personToDelete);

        assertCommandSuccess(deleteDocCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteDocCommand deleteDocCommand = new DeleteDocCommand(outOfBoundIndex);

        assertCommandFailure(deleteDocCommand, model, Messages.MESSAGE_INVALID_DOCTOR_DISPLAYED_INDEX);
    }

    @Test
    public void execute_personToDeleteNotDoctor_throwsCommandException() {
        Model noDoctorModel = new ModelManager();
        Patient patient = new PatientBuilder().withName("Alice")
                .withPhone("11111111")
                .withEmail("alice@tan.com")
                .withAddress("11 Conlins St").build();

        noDoctorModel.addPatient(patient);
        DeleteDocCommand delDocCommand = new DeleteDocCommand(INDEX_FIRST_PERSON);
        assertCommandFailure(delDocCommand, noDoctorModel, "The person at the specified index is not a doctor.");
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteDocCommand deleteDocCommand = new DeleteDocCommand(outOfBoundIndex);

        assertCommandFailure(deleteDocCommand, model, Messages.MESSAGE_INVALID_DOCTOR_DISPLAYED_INDEX);
    }

    // test added by copilot
    @Test
    public void execute_scheduleIoError_throwsCommandException() throws Exception {
        File scheduleFile = new File("data/schedule.json");
        byte[] backup = Files.readAllBytes(scheduleFile.toPath());
        try {
            Files.writeString(scheduleFile.toPath(), "not valid json");
            DeleteDocCommand deleteDocCommand = new DeleteDocCommand(INDEX_FIRST_PERSON);
            assertThrows(CommandException.class, () -> deleteDocCommand.execute(model));
        } finally {
            Files.write(scheduleFile.toPath(), backup);
        }
    }

    @Test
    public void equals() {
        DeleteDocCommand deleteFirstCommand = new DeleteDocCommand(INDEX_FIRST_PERSON);
        DeleteDocCommand deleteSecondCommand = new DeleteDocCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteDocCommand deleteFirstCommandCopy = new DeleteDocCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteDocCommand deleteDocCommand = new DeleteDocCommand(targetIndex);
        String expected = DeleteDocCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteDocCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
