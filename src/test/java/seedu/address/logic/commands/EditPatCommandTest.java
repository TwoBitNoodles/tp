package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_JOHN;
import static seedu.address.logic.commands.CommandTestUtil.DESC_SAM;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPatients.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Patient;
import seedu.address.testutil.EditPatientDescriptorBuilder;
import seedu.address.testutil.PatientBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code EditPatCommand}.
 */
public class EditPatCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalAddressBook(),
        getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        model = new ModelManager();
        Patient originalPatient = new PatientBuilder().build();
        model.addPatient(originalPatient);

        Patient editedPatient = new PatientBuilder()
            .withName("John Doe")
            .withPhone("91234567")
            .withEmail("JD@gmail.com")
            .withAddress("123, Jurong West Ave 6, #08-111").build();

        EditPatCommand.EditPatDescriptor descriptor = new EditPatientDescriptorBuilder(editedPatient).build();
        EditPatCommand editPatCommand = new EditPatCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditPatCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), model.getPatientData(),
                model.getDoctorData(), new UserPrefs());

        Patient patientInExpected = (Patient) expectedModel.getFilteredPersonList().get(0);
        expectedModel.setPatient(patientInExpected, editedPatient);
        assertCommandSuccess(editPatCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        model = new ModelManager();
        Patient originalPatient = new PatientBuilder().build();
        model.addPatient(originalPatient);

        Patient editedPatient = new PatientBuilder()
            .withName("John Doe")
            .withAddress("123, Jurong West Ave 6, #08-111").build();

        EditPatCommand.EditPatDescriptor descriptor = new EditPatientDescriptorBuilder(editedPatient).build();
        EditPatCommand editPatCommand = new EditPatCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditPatCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
            Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), model.getPatientData(),
            model.getDoctorData(), new UserPrefs());

        Patient patientInExpected = (Patient) expectedModel.getFilteredPersonList().get(0);
        expectedModel.setPatient(patientInExpected, editedPatient);
        assertCommandSuccess(editPatCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Patient patientInFilteredList = (Patient) model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Patient editedPatient = new PatientBuilder(patientInFilteredList).withName(VALID_NAME_BOB).build();
        EditPatCommand editPatCommand = new EditPatCommand(INDEX_FIRST_PERSON,
            new EditPatientDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditPatCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
            Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), model.getPatientData(),
            model.getDoctorData(), new UserPrefs());
        expectedModel.setPatient((Patient) model.getFilteredPersonList().get(0), editedPatient);

        assertCommandSuccess(editPatCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Patient firstPatient = (Patient) model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPatCommand.EditPatDescriptor descriptor = new EditPatientDescriptorBuilder(firstPatient).build();
        EditPatCommand editPatCommand = new EditPatCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editPatCommand, model, EditPatCommand.MESSAGE_DUPLICATE_PATIENT);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Patient patientInList = (Patient) model.getAddressBook().getPersonList()
            .get(INDEX_SECOND_PERSON.getZeroBased());
        EditPatCommand editPatCommand = new EditPatCommand(INDEX_FIRST_PERSON,
            new EditPatientDescriptorBuilder(patientInList).build());

        assertCommandFailure(editPatCommand, model, EditPatCommand.MESSAGE_DUPLICATE_PATIENT);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPatCommand.EditPatDescriptor descriptor = new EditPatientDescriptorBuilder()
            .withName(VALID_NAME_BOB).build();
        EditPatCommand editPatCommand = new EditPatCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editPatCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditPatCommand editPatCommand = new EditPatCommand(outOfBoundIndex,
            new EditPatientDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editPatCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditPatCommand standardCommand = new EditPatCommand(INDEX_FIRST_PERSON, DESC_JOHN);

        // same values -> returns true
        EditPatCommand.EditPatDescriptor copyDescriptor = new EditPatCommand.EditPatDescriptor(DESC_JOHN);
        EditPatCommand commandWithSameValues = new EditPatCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditPatCommand(INDEX_SECOND_PERSON, DESC_JOHN)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditPatCommand(INDEX_FIRST_PERSON, DESC_SAM)));
    }


    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPatCommand.EditPatDescriptor editPatDescriptor = new EditPatCommand.EditPatDescriptor();
        EditPatCommand editPatCommand = new EditPatCommand(index, editPatDescriptor);
        String expected = EditPatCommand.class.getCanonicalName() + "{index=" + index + ", editPatDescriptor="
            + editPatDescriptor + "}";
        assertEquals(expected, editPatCommand.toString());
    }



}
