package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.storage.ScheduleManager;

/**
 * Allows viewing schedules in the app.
 */
public class ViewSchedCommand extends Command {

    public static final String COMMAND_WORD = "viewsched";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Views the schedule of a doctor (optionally for a specific date).\n"
            + "Parameters: d/DOCTOR_NAME [date/YYYY-MM-DD]\n"
            + "Example: " + COMMAND_WORD + " d/John Tan date/2026-03-20";

    public static final String MESSAGE_SUCCESS = "Schedule for %1$s on %2$s\n\n";

    public static final String MESSAGE_DOCTOR_NOT_FOUND = "Doctor not found.";
    public static final String MESSAGE_DATE_NOT_AVAILABLE = "No schedule available for this date.";
    public static final String MESSAGE_INVALID_DOCTOR_NAME =
            "The specified name belongs to a patient. Please enter a doctor's name.";

    private final String doctorName;
    private final LocalDate date;

    /**
     * Creates a ViewSchedCommand to view doctors' schedules.
     */
    public ViewSchedCommand(String doctorName, LocalDate date) {
        this.doctorName = normalizeSpaces(doctorName);
        this.date = date;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            if (isPatientName(model) && !isDoctorName(model)) {
                throw new CommandException(MESSAGE_INVALID_DOCTOR_NAME);
            }

            if (date != null) {
                // Single day behavior
                Map<String, String> schedule =
                        ScheduleManager.getScheduleIgnoreCase(doctorName, date.toString());

                if (schedule == null) {
                    return new CommandResult(MESSAGE_DOCTOR_NOT_FOUND);
                }

                return new CommandResult(
                        String.format(MESSAGE_SUCCESS, doctorName, date),
                        schedule
                );

            } else {
                // Weekly behavior: collect schedules for 7 days
                Map<String, Map<String, String>> weeklySchedule = new LinkedHashMap<>();

                if (!isDoctorName(model)) {
                    return new CommandResult(MESSAGE_DOCTOR_NOT_FOUND);
                }

                LocalDate today = LocalDate.now();
                for (int i = 0; i < 7; i++) {
                    LocalDate d = today.plusDays(i);
                    Map<String, String> schedule =
                            ScheduleManager.getScheduleIgnoreCase(doctorName, d.toString());
                    weeklySchedule.put(d.toString(), schedule);
                }

                return new CommandResult(
                        "Weekly schedule for " + doctorName,
                        weeklySchedule, true
                );
            }
        } catch (IllegalArgumentException e) {
            return new CommandResult(MESSAGE_DATE_NOT_AVAILABLE);
        }
    }

    /**
     * Normalizes spacing in doctor names.
     */
    private String normalizeSpaces(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private boolean isDoctorName(Model model) {
        return model.getDoctorData().getPersonList().stream()
                .map(Person::getName)
                .anyMatch(name -> name.fullName.equalsIgnoreCase(doctorName));
    }

    private boolean isPatientName(Model model) {
        return model.getPatientData().getPersonList().stream()
                .map(Person::getName)
                .anyMatch(name -> name.fullName.equalsIgnoreCase(doctorName));
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewSchedCommand)) {
            return false;
        }

        ViewSchedCommand otherCommand = (ViewSchedCommand) other;
        return doctorName.equals(otherCommand.doctorName)
                && Objects.equals(date, otherCommand.date);
    }
}
