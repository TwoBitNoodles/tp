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

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.ScheduleManager;

/**
 * Edits the details of an existing doctor in the app.
 */
public class EditDocCommand extends Command {

    public static final String COMMAND_WORD = "editdoc";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the doctor identified "
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

    public static final String MESSAGE_EDIT_DOCTOR_SUCCESS = "Edited Doctor: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_DOCTOR = "This doctor already exists in the app.";

    private final Index index;
    private final EditDoctorDescriptor editDoctorDescriptor;

    /**
     * @param index of the doctor in the filtered doctor list to edit
     * @param editDoctorDescriptor details to edit the doctor with
     */
    public EditDocCommand(Index index, EditDoctorDescriptor editDoctorDescriptor) {
        requireNonNull(index);
        requireNonNull(editDoctorDescriptor);

        this.index = index;
        this.editDoctorDescriptor = new EditDoctorDescriptor(editDoctorDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<? extends Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_DOCTOR_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        if (!(personToEdit instanceof Doctor)) {
            throw new CommandException("The person at the specified index is not a doctor.");
        }

        Doctor doctorToEdit = (Doctor) personToEdit;
        Doctor editedDoctor = createEditedDoctor(doctorToEdit, editDoctorDescriptor);

        if (!doctorToEdit.isSamePerson(editedDoctor) && model.hasDoctor(editedDoctor)) {
            throw new CommandException(MESSAGE_DUPLICATE_DOCTOR);
        }

        String currDoctorName = doctorToEdit.getName().fullName;
        String newDoctorName = editedDoctor.getName().fullName;

        model.setDoctor(doctorToEdit, editedDoctor);

        if (!currDoctorName.equalsIgnoreCase(newDoctorName)) {
            try {
                ScheduleManager.renameDoctorSchedule(currDoctorName, newDoctorName);
            } catch (java.io.IOException e) {
                throw new CommandException("Failed to update schedule file.");
            }
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_DOCTOR_SUCCESS, Messages.format(editedDoctor)));
    }

    /**
     * Creates and returns a {@code Doctor} with the details of {@code doctorToEdit}
     * edited with {@code editDoctorDescriptor}.
     */
    private static Doctor createEditedDoctor(Person doctorToEdit, EditDoctorDescriptor editDoctorDescriptor) {
        requireNonNull(doctorToEdit);

        Name updatedName = editDoctorDescriptor.getName().orElse(doctorToEdit.getName());
        Phone updatedPhone = editDoctorDescriptor.getPhone().orElse(doctorToEdit.getPhone());
        Email updatedEmail = editDoctorDescriptor.getEmail().orElse(doctorToEdit.getEmail());
        Address updatedAddress = editDoctorDescriptor.getAddress().orElse(doctorToEdit.getAddress());

        return new Doctor(updatedName, updatedPhone, updatedEmail, updatedAddress);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditDocCommand)) {
            return false;
        }

        EditDocCommand otherEditCommand = (EditDocCommand) other;
        return index.equals(otherEditCommand.index)
                && editDoctorDescriptor.equals(otherEditCommand.editDoctorDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editDoctorDescriptor", editDoctorDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the doctor with. Each non-empty field value will replace the
     * corresponding field value of the doctor.
     */
    public static class EditDoctorDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;

        public EditDoctorDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditDoctorDescriptor(EditDoctorDescriptor toCopy) {
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
            if (!(other instanceof EditDoctorDescriptor)) {
                return false;
            }

            EditDoctorDescriptor otherEditDoctorDescriptor = (EditDoctorDescriptor) other;
            return Objects.equals(name, otherEditDoctorDescriptor.name)
                    && Objects.equals(phone, otherEditDoctorDescriptor.phone)
                    && Objects.equals(email, otherEditDoctorDescriptor.email)
                    && Objects.equals(address, otherEditDoctorDescriptor.address);
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
