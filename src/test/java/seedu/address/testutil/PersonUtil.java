package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.logic.commands.AddDocCommand;
import seedu.address.logic.commands.AddPatCommand;
import seedu.address.logic.commands.EditDocCommand.EditDoctorDescriptor;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code doctor}.
     */
    public static String getDocAddCommand(Doctor doctor) {
        return AddDocCommand.COMMAND_WORD + " " + getPersonDetails(doctor);
    }

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getPatAddCommand(Patient patient) {
        return AddPatCommand.COMMAND_WORD + " " + getPersonDetails(patient);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().fullName + " ");
        sb.append(PREFIX_PHONE + person.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + person.getAddress().value + " ");
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditDoctorDescriptor}'s details.
     */
    public static String getEditDoctorDescriptorDetails(EditDoctorDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(address.value).append(" "));
        return sb.toString();
    }
}
