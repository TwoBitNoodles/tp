package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOCTOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.io.IOException;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
/**
 * Adds an Appointment to the app.
 */
public class AddApptCommand extends Command {
    public static final String COMMAND_WORD = "addappt";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds an Appointment for a person at the specified date and time.\n"
            + "Parameters: "
            + PREFIX_DOCTOR + " DOCTOR NAME"
            + PREFIX_NAME + " NAME"
            + PREFIX_DATE + " DATE (yyyy-mm-dd)"
            + PREFIX_TIME + " TIME (H:MM)\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_DOCTOR + "Sally Tan"
            + PREFIX_NAME + " John Doe "
            + PREFIX_DATE + " 2026-03-11 "
            + PREFIX_TIME + " 9:00 ";

    public static final String MESSAGE_SUCCESS = "New appointment added!";
    public static final String MESSAGE_DUPLICATE_APPT = "This appointment already exists in the address book";


    private final Appointment toAdd;

    /**
     * Creates an AddApptCommand to add the specified Appointment details.
     */
    public AddApptCommand(Appointment appt) {
        requireNonNull(appt);
        toAdd = appt;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            model.addAppt(toAdd);
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof AddApptCommand)) {
            return false;
        }

        AddApptCommand otherCommand = (AddApptCommand) o;
        return toAdd.equals(otherCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
