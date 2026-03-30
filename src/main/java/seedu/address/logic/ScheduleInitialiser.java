package seedu.address.logic;

import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.person.Doctor;
import seedu.address.storage.ScheduleManager;

/**
 * Initializes the schedule JSON file with default schedules for all doctors in the address book.
 */
public class ScheduleInitialiser {

    /**
     * Synchronises the schedule file so it always covers the current 7-day window.
     */
    public static void initialize(Model model) {
        List<String> doctorNames = model.getFilteredPersonList().stream()
                .filter(p -> p instanceof Doctor)
                .map(p -> ((Doctor) p).getName().fullName)
                .toList();

        ScheduleManager.syncSchedules(doctorNames);
    }
}
