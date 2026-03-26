package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalDoctors.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditDocCommand.EditDoctorDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Doctor;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.EditDoctorDescriptorBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditDocCommand.
 */
public class EditDocCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalAddressBook(),
            getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Doctor editedDoctor = new DoctorBuilder().build();
        EditDoctorDescriptor descriptor = new EditDoctorDescriptorBuilder(editedDoctor).build();
        EditDocCommand editDocCommand = new EditDocCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditDocCommand.MESSAGE_EDIT_DOCTOR_SUCCESS,
                Messages.format(editedDoctor));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), model.getPatientData(),
                model.getDoctorData(), new UserPrefs());
        expectedModel.setDoctor((Doctor) model.getFilteredPersonList().get(0), editedDoctor);

        assertCommandSuccess(editDocCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastDoctor = Index.fromOneBased(model.getFilteredPersonList().size());
        Doctor lastDoctor = (Doctor) model.getFilteredPersonList().get(indexLastDoctor.getZeroBased());

        DoctorBuilder doctorInList = new DoctorBuilder(lastDoctor);
        Doctor editedDoctor = (Doctor) doctorInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB).build();

        EditDoctorDescriptor descriptor = new EditDoctorDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).build();
        EditDocCommand editDocCommand = new EditDocCommand(indexLastDoctor, descriptor);

        String expectedMessage = String.format(EditDocCommand.MESSAGE_EDIT_DOCTOR_SUCCESS,
                Messages.format(editedDoctor));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), model.getPatientData(),
                model.getDoctorData(), new UserPrefs());
        expectedModel.setDoctor(lastDoctor, editedDoctor);

        assertCommandSuccess(editDocCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditDocCommand editDocCommand = new EditDocCommand(INDEX_FIRST_PERSON, new EditDoctorDescriptor());
        Doctor editedDoctor = (Doctor) model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditDocCommand.MESSAGE_EDIT_DOCTOR_SUCCESS,
                Messages.format(editedDoctor));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), model.getPatientData(),
                model.getDoctorData(), new UserPrefs());

        assertCommandSuccess(editDocCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Doctor doctorInFilteredList = (Doctor) model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Doctor editedDoctor = new DoctorBuilder(doctorInFilteredList).withName(VALID_NAME_BOB).build();
        EditDocCommand editDocCommand = new EditDocCommand(INDEX_FIRST_PERSON,
                new EditDoctorDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditDocCommand.MESSAGE_EDIT_DOCTOR_SUCCESS,
                Messages.format(editedDoctor));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), model.getPatientData(),
                model.getDoctorData(), new UserPrefs());
        expectedModel.setDoctor((Doctor) model.getFilteredPersonList().get(0), editedDoctor);

        assertCommandSuccess(editDocCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Doctor firstDoctor = (Doctor) model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditDoctorDescriptor descriptor = new EditDoctorDescriptorBuilder(firstDoctor).build();
        EditDocCommand editDocCommand = new EditDocCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editDocCommand, model, EditDocCommand.MESSAGE_DUPLICATE_DOCTOR);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit doctor in filtered list into a duplicate in app
        Doctor doctorInList = (Doctor) model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditDocCommand editDocCommand = new EditDocCommand(INDEX_FIRST_PERSON,
                new EditDoctorDescriptorBuilder(doctorInList).build());

        assertCommandFailure(editDocCommand, model, EditDocCommand.MESSAGE_DUPLICATE_DOCTOR);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditDoctorDescriptor descriptor = new EditDoctorDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditDocCommand editDocCommand = new EditDocCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editDocCommand, model, Messages.MESSAGE_INVALID_DOCTOR_DISPLAYED_INDEX);
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

        EditDocCommand editDocCommand = new EditDocCommand(outOfBoundIndex,
                new EditDoctorDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editDocCommand, model, Messages.MESSAGE_INVALID_DOCTOR_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditDocCommand standardCommand = new EditDocCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditDoctorDescriptor copyDescriptor = new EditDoctorDescriptor(DESC_AMY);
        EditDocCommand commandWithSameValues = new EditDocCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditDocCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditDocCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditDoctorDescriptor editDoctorDescriptor = new EditDoctorDescriptor();
        EditDocCommand editDocCommand = new EditDocCommand(index, editDoctorDescriptor);
        String expected = EditDocCommand.class.getCanonicalName() + "{index=" + index + ", editDoctorDescriptor="
                + editDoctorDescriptor + "}";
        assertEquals(expected, editDocCommand.toString());
    }

}
