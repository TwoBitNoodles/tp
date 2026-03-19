package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOCTOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;

/**
 * Deletes an appointment identified using the doctor name, and the date and time the appointment falls on
 */
public class DeleteApptCommand extends Command {
    public static final String COMMAND_WORD = "delappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the the appointment identified by the date and time in a specific doctor's schedule.\n"
            + "Parameters: "
            + PREFIX_DOCTOR + "DOCTOR NAME"
            + PREFIX_NAME + "PATIENT NAME"
            + PREFIX_DATE + "DATE (yyyy-mm-dd)"
            + PREFIX_TIME + "TIME  (H:MM)\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_DOCTOR + " John Doe "
            + PREFIX_DATE + " 2026-03-11 "
            + PREFIX_TIME + " 9:00 ";
    public static final String MESSAGE_SUCCESS = "Appointment deleted!";

    private final Appointment toDel;

    public DeleteApptCommand(Appointment appt) {
        this.toDel = appt;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        model.delAppt(toDel);
        return new CommandResult(MESSAGE_SUCCESS);

    }
}
