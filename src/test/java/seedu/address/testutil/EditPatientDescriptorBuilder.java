package seedu.address.testutil;

import seedu.address.logic.commands.EditPatCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Phone;

/**
 * A utility class to help with building EditPatDescriptor objects.
 */
public class EditPatientDescriptorBuilder {
    private EditPatCommand.EditPatDescriptor descriptor;

    public EditPatientDescriptorBuilder() {
        descriptor = new EditPatCommand.EditPatDescriptor();
    }

    public EditPatientDescriptorBuilder(EditPatCommand.EditPatDescriptor descriptor) {
        this.descriptor = new EditPatCommand.EditPatDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPatDescriptor} with fields containing {@code patient}'s details
     */
    public EditPatientDescriptorBuilder(Patient patient) {
        descriptor = new EditPatCommand.EditPatDescriptor();
        descriptor.setName(patient.getName());
        descriptor.setPhone(patient.getPhone());
        descriptor.setEmail(patient.getEmail());
        descriptor.setAddress(patient.getAddress());
    }

    /**
     * Sets the {@code Name} of the {@code EditPatDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPatDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPatDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditPatDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    public EditPatCommand.EditPatDescriptor build() {
        return descriptor;
    }
}
