package seedu.address.logic.commands;

import java.time.LocalDate;
import java.util.Map;

import seedu.address.model.Model;
import seedu.address.storage.ScheduleManager;

/**
 * Allows viewing schedules in the app.
 */
public class ViewSchedCommand extends Command {

    public static final String COMMAND_WORD = "viewsched";

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
    public CommandResult execute(Model model) {

        try {
            Map<String, String> schedule =
                    ScheduleManager.getScheduleIgnoreCase(doctorName, date.toString());

            if (schedule == null) {
                return new CommandResult("Doctor not found.");
            }

            StringBuilder result = new StringBuilder();
            result.append("Schedule for ")
                    .append(doctorName)
                    .append(" on ")
                    .append(date)
                    .append("\n\n");

            for (Map.Entry<String, String> slot : schedule.entrySet()) {
                if (slot.getValue() == null) {
                    result.append(slot.getKey()).append(" – Available\n");
                } else {
                    result.append(slot.getKey()).append(" – Booked\n");
                }
            }

            return new CommandResult(result.toString());

        } catch (IllegalArgumentException e) {
            return new CommandResult("No schedule available for this date.");
        }
    }
    private String normalizeSpaces(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }
}
