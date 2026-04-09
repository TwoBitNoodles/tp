package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPT_ID;

import java.io.IOException;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.storage.AppointmentManager;

/**
 * Deletes an appointment identified by its appointment id.
 */
public class DeleteApptCommand extends Command {
    public static final String COMMAND_WORD = "delappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the appointment identified by its id.\n"
            + "Parameters: "
            + PREFIX_APPT_ID + "APPOINTMENT_ID\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_APPT_ID + "3";
    public static final String MESSAGE_SUCCESS = "Appointment deleted!";

    private final int apptId;

    /**
     * Creates a command to delete the appointment with the specified ID.
     */
    public DeleteApptCommand(int apptId) {
        this.apptId = apptId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            Appointment appt = AppointmentManager.getAppointmentById(apptId);
            if (appt == null) {
                throw new CommandException("Appointment id not found: " + apptId);
            }

            model.delAppt(appt);
            AppointmentManager.deleteAppointment(apptId);
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
