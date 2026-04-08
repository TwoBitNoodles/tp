package seedu.address.testutil;

import seedu.address.logic.commands.EditDocCommand.EditDoctorDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;


/**
 * A utility class to help with building EditDoctorDescriptor objects.
 */
public class EditDoctorDescriptorBuilder {

    private EditDoctorDescriptor descriptor;

    public EditDoctorDescriptorBuilder() {
        descriptor = new EditDoctorDescriptor();
    }

    public EditDoctorDescriptorBuilder(EditDoctorDescriptor descriptor) {
        this.descriptor = new EditDoctorDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditDoctorDescriptor} with fields containing {@code doctor}'s details
     */
    public EditDoctorDescriptorBuilder(Doctor doctor) {
        descriptor = new EditDoctorDescriptor();
        descriptor.setName(doctor.getName());
        descriptor.setPhone(doctor.getPhone());
        descriptor.setEmail(doctor.getEmail());
        descriptor.setAddress(doctor.getAddress());
    }

    /**
     * Sets the {@code Name} of the {@code EditDoctorDescriptor} that we are building.
     */
    public EditDoctorDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditDoctorDescriptor} that we are building.
     */
    public EditDoctorDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditDoctorDescriptor} that we are building.
     */
    public EditDoctorDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditDoctorDescriptor} that we are building.
     */
    public EditDoctorDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    public EditDoctorDescriptor build() {
        return descriptor;
    }
}
