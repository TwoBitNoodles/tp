package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOCTOR_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.io.IOException;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.storage.AppointmentManager;
/**
 * Adds an Appointment to the app.
 */
public class AddApptCommand extends Command {
    public static final String COMMAND_WORD = "addappt";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds an Appointment for a person at the specified date and time.\n"
            + "Parameters: "
            + PREFIX_DOCTOR_ID + " DOCTOR_ID"
            + PREFIX_PATIENT_ID + " PATIENT_ID"
            + PREFIX_DATE + " DATE (yyyy-mm-dd)"
            + PREFIX_TIME + " TIME (H:MM)\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_DOCTOR_ID + "1"
            + PREFIX_PATIENT_ID + " 3 "
            + PREFIX_DATE + " 2026-03-11 "
            + PREFIX_TIME + " 9:00 ";

    public static final String MESSAGE_SUCCESS = "New appointment added! ID: %1$d";
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
            AppointmentManager.addAppointment(toAdd);
        } catch (IOException e) {
            // Best-effort rollback if one of the two persistence steps fails.
            try {
                if (toAdd.getApptID() != Appointment.UNASSIGNED_ID) {
                    AppointmentManager.deleteAppointment(toAdd.getApptID());
                }
            } catch (IOException ignored) {
                // Ignore rollback failure.
            }

            try {
                model.delAppt(toAdd);
            } catch (IOException ignored) {
                // Ignore rollback failure.
            }
            throw new CommandException(e.getMessage());
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getApptID()));
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
