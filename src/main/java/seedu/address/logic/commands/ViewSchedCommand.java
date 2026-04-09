package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Doctor;
import seedu.address.storage.ScheduleManager;

/**
 * Allows viewing schedules in the app.
 */
public class ViewSchedCommand extends Command {

    public static final String COMMAND_WORD = "viewsched";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Views the schedule of a doctor (optionally for a specific date).\n"
            + "Parameters: d/DOCTOR_NAME id/DOCTOR_ID [date/YYYY-MM-DD]\n"
            + "Example: " + COMMAND_WORD + " d/John Tan id/1 date/2026-03-20";

    public static final String MESSAGE_SUCCESS = "Schedule for %1$s (ID: %2$d) on %3$s\n\n";

    public static final String MESSAGE_DOCTOR_NOT_FOUND = "Doctor not found.";
    public static final String MESSAGE_DATE_NOT_AVAILABLE = "No schedule available for this date.";
    public static final String MESSAGE_WEEKLY_SUCCESS = "Weekly schedule for %1$s (ID: %2$d)";

    private static final int SCHEDULE_WINDOW_DAYS = 7;
    private final String doctorName;
    private final int doctorId;
    private final LocalDate date;

    /**
     * Creates a ViewSchedCommand to view doctors' schedules.
     */
    public ViewSchedCommand(String doctorName, int doctorId, LocalDate date) {
        this.doctorName = normalizeSpaces(doctorName);
        this.doctorId = doctorId;
        this.date = date;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Doctor doctor = findDoctorByNameAndId(model);
        if (doctor == null) {
            return new CommandResult(MESSAGE_DOCTOR_NOT_FOUND);
        }

        try {
            if (date != null) {
                return executeSingleDay();
            }

            return executeWeekly();
        } catch (IllegalArgumentException e) {
            return new CommandResult(MESSAGE_DATE_NOT_AVAILABLE);
        }
    }

    private CommandResult executeSingleDay() {
        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(doctorId, date.toString());

        if (schedule == null) {
            return new CommandResult(MESSAGE_DOCTOR_NOT_FOUND);
        }

        return new CommandResult(
                String.format(MESSAGE_SUCCESS, doctorName, doctorId, date),
                schedule, doctorName, doctorId, date
        );
    }

    private CommandResult executeWeekly() {
        Map<String, Map<String, String>> weeklySchedule = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < SCHEDULE_WINDOW_DAYS; i++) {
            LocalDate d = today.plusDays(i);
            Map<String, String> schedule = ScheduleManager.getScheduleByDocId(doctorId, d.toString());
            if (schedule == null) {
                return new CommandResult(MESSAGE_DATE_NOT_AVAILABLE);
            }
            weeklySchedule.put(d.toString(), schedule);
        }

        return new CommandResult(
                String.format(MESSAGE_WEEKLY_SUCCESS, doctorName, doctorId),
                weeklySchedule, true, doctorName, doctorId
        );
    }

    /**
     * Normalizes spacing in doctor names.
     */
    private String normalizeSpaces(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private Doctor findDoctorByNameAndId(Model model) {
        return model.getDoctorData().getPersonList().stream()
                .filter(person -> person instanceof Doctor)
                .map(person -> (Doctor) person)
                .filter(doctor -> doctor.getDocId() == doctorId
                        && doctor.getName().fullName.equalsIgnoreCase(doctorName))
                .findFirst()
                .orElse(null);
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
                && doctorId == otherCommand.doctorId
                && Objects.equals(date, otherCommand.date);
    }
}
