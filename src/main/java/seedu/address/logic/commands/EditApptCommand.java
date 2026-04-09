package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDOC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWTIME;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.storage.AppointmentManager;
/**
 * Edits an existing appointment
 */

public class EditApptCommand extends Command {
    public static final String COMMAND_WORD = "editappt";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": changes the doctor/date/time of an appointment"
            + " Identifies the appointment by id."
            + "Paramters: "
            + PREFIX_APPT_ID + "APPOINTMENT_ID\n"
            + "[" + PREFIX_NEWDOC + "NEW_DOCTOR_ID] "
            + "[" + PREFIX_NEWDATE + "NEW_DATE] "
            + "[" + PREFIX_NEWTIME + "NEW_TIME]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_APPT_ID + "3 "
            + PREFIX_NEWTIME + "11:30";

    public static final String MESSAGE_SUCCESS = "Edited appointment!";
    public static final String MESSAGE_NO_CHANGES = "No changes made to the appointment.";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter STORAGE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final int apptId;
    private final String newDoc;
    private final String newDate;
    private final String newTime;

    /**
     * Creates a command to edit an appointment with new details.
     * At least one new field must be provided (enforced by parser).
     */
    public EditApptCommand(int apptId, String newDoc, String newDate, String newTime) {
        this.apptId = apptId;
        this.newDoc = newDoc;
        this.newDate = newDate;
        this.newTime = newTime;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            Appointment oldAppt = AppointmentManager.getAppointmentById(apptId);
            if (oldAppt == null) {
                throw new CommandException("Appointment id not found: " + apptId);
            }

            if (hasNoChangesSafely(oldAppt)) {
                return new CommandResult(MESSAGE_NO_CHANGES);
            }

            Appointment editedAppt = model.editAppt(oldAppt, newDoc, newDate, newTime);
            AppointmentManager.updateAppointment(apptId, editedAppt);
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (IOException e) {
            throw new CommandException("Could not edit appointment: " + e.getMessage());
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new CommandException("Could not edit appointment: " + e.getMessage());
        }
    }

    private boolean hasNoChangesSafely(Appointment oldAppt) {
        try {
            return hasNoChanges(oldAppt);
        } catch (NumberFormatException | DateTimeParseException e) {
            return false;
        }
    }

    private boolean hasNoChanges(Appointment oldAppt) {
        int finalDocId = oldAppt.getDocId();
        if (newDoc != null) {
            finalDocId = Integer.parseInt(newDoc.trim());
        }

        String finalDate = (newDate != null) ? newDate : oldAppt.getDate();
        String finalTime = (newTime != null) ? normalizeTime(newTime) : normalizeTime(oldAppt.getTime());

        return finalDocId == oldAppt.getDocId()
                && finalDate.equals(oldAppt.getDate())
                && finalTime.equals(normalizeTime(oldAppt.getTime()));
    }

    private String normalizeTime(String time) {
        LocalTime parsedTime = LocalTime.parse(time, TIME_FORMAT);
        return parsedTime.format(STORAGE_TIME_FORMAT);
    }

}
