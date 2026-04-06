package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Patient;
import seedu.address.testutil.PatientBuilder;

/**
 * Contains unit tests for {@code AddPatCommand}.
 */
public class AddPatCommandTest {

    private Model model;

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddPatCommand(null));
    }

    @Test
    public void execute_validPerson_success() {
        model = new ModelManager();
        Patient validPatient = new PatientBuilder().build();
        AddPatCommand addPatCommand = new AddPatCommand(validPatient);
        Model expectedModel = new ModelManager();
        expectedModel.addPatient(validPatient);

        assertCommandSuccess(addPatCommand, model,
                String.format(AddPatCommand.MESSAGE_SUCCESS, Messages.format(validPatient)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePatient_throwsCommandException() {
        model = new ModelManager();
        Patient patient = new PatientBuilder().build();

        model.addPatient(patient); // already exists

        AddPatCommand addPatCommand = new AddPatCommand(patient);

        assertCommandFailure(addPatCommand, model,
                AddPatCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void equals() {
        Patient alice = new PatientBuilder().withName("Alice").build();
        Patient bob = new PatientBuilder().withName("Bob").build();
        AddPatCommand addAliceCommand = new AddPatCommand(alice);
        AddPatCommand addBobCommand = new AddPatCommand(bob);

        // same object then returns true
        assert addAliceCommand.equals(addAliceCommand);

        // same values then returns true
        AddPatCommand addAliceCommandCopy = new AddPatCommand(alice);
        assert addAliceCommand.equals(addAliceCommandCopy);

        // different types then returns false
        assert !addAliceCommand.equals(1);

        // different person then returns false
        assert !addAliceCommand.equals(addBobCommand);
    }

    @Test
    public void toString_returnsCorrectFormat() {
        Patient alice = new PatientBuilder().withName("Alice")
                .withPhone("98765432")
                .withEmail("alice@example.com")
                .withAddress("123 Street")
                .build();

        String actual = alice.toString();
        assertTrue(actual.contains("Alice"));
        assertTrue(actual.contains("98765432"));
        assertTrue(actual.contains("123 Street"));
        assertTrue(actual.contains("alice@example.com"));
    }
}
