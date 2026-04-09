package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
* Edits the details of an existing patient in the app.
*/
public class EditPatCommand extends Command {
    public static final String COMMAND_WORD = "editpat";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the patient identified "
            + "by the index number used in the displayed list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PATIENT_SUCCESS = "Edited Patient: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PATIENT = "This patient already exists in the app.";
    private static final Logger logger = Logger.getLogger(EditPatCommand.class.getName());

    private final Index index;
    private final EditPatCommand.EditPatDescriptor editPatDescriptor;

    /**
     * @param index of the patient in the filtered patient list to edit
     * @param editPatDescriptor details to edit the patient with
     */
    public EditPatCommand(Index index, EditPatCommand.EditPatDescriptor editPatDescriptor) {
        requireNonNull(index);
        requireNonNull(editPatDescriptor);

        this.index = index;
        this.editPatDescriptor = new EditPatCommand.EditPatDescriptor(editPatDescriptor);
        logger.info("Created EditPatCommand with index: " + index + " and editPatDescriptor: " + editPatDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<? extends Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        if (!(personToEdit instanceof Patient)) {
            throw new CommandException("The person at the specified index is not a patient.");
        }

        if (!editPatDescriptor.isAnyFieldEdited()) {
            throw new CommandException(EditPatCommand.MESSAGE_NOT_EDITED);
        }

        Patient patientToEdit = (Patient) personToEdit;
        Patient editedPatient = createEditedPatient(patientToEdit, editPatDescriptor);

        if (!patientToEdit.isSamePerson(editedPatient) && model.hasPatient(editedPatient)) {
            throw new CommandException(MESSAGE_DUPLICATE_PATIENT);
        }

        model.setPatient(patientToEdit, editedPatient);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PATIENT_SUCCESS, Messages.format(editedPatient)));
    }

    /**
     * Creates and returns a {@code Patient} with the details of {@code patientToEdit}
     * edited with {@code editPatDescriptor}.
     */
    private static Patient createEditedPatient(Patient patientToEdit,
                                               EditPatCommand.EditPatDescriptor editPatDescriptor) {
        assert patientToEdit != null;

        Name updatedName = editPatDescriptor.getName().orElse(patientToEdit.getName());
        Phone updatedPhone = editPatDescriptor.getPhone().orElse(patientToEdit.getPhone());
        Email updatedEmail = editPatDescriptor.getEmail().orElse(patientToEdit.getEmail());
        Address updatedAddress = editPatDescriptor.getAddress().orElse(patientToEdit.getAddress());

        return new Patient(updatedName, updatedPhone, updatedEmail, updatedAddress);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditPatCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                    && editPatDescriptor.equals(otherEditCommand.editPatDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPatDescriptor", editPatDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the patient with. Each non-empty field value will replace the
     * corresponding field value of the patient.
     */
    public static class EditPatDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;

        public EditPatDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPatDescriptor(EditPatCommand.EditPatDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPatDescriptor otherEditPatDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPatDescriptor.name)
                            && Objects.equals(phone, otherEditPatDescriptor.phone)
                            && Objects.equals(email, otherEditPatDescriptor.email)
                            && Objects.equals(address, otherEditPatDescriptor.address);
        }

        @Override
            public String toString() {
            return new ToStringBuilder(this)
                            .add("name", name)
                            .add("phone", phone)
                            .add("email", email)
                            .add("address", address)
                            .toString();
        }
    }
}
