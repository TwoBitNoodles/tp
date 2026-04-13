package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + "}";
        assertEquals(expected, commandResult.toString());
    }

    @Test
    public void constructor_withScheduleMap() {
        Map<String, String> schedule = new java.util.LinkedHashMap<>();
        schedule.put("09:00", "Alice");
        CommandResult result = new CommandResult("feedback", schedule);
        assertEquals("feedback", result.getFeedbackToUser());
        assertEquals(schedule, result.getSchedule());
    }

    @Test
    public void constructor_withWeeklySchedule() {
        Map<String, Map<String, String>> weekly = new java.util.LinkedHashMap<>();
        weekly.put("Monday", new java.util.LinkedHashMap<>());
        CommandResult result = new CommandResult("feedback", weekly, true);
        assertTrue(result.isWeekly());
        assertEquals(weekly, result.getWeeklySchedule());
    }

    @Test
    public void getScheduleMetadata() {
        Map<String, String> schedule = new java.util.LinkedHashMap<>();
        schedule.put("09:00", "Alice");
        CommandResult result = new CommandResult("feedback", schedule,
                "Dr Smith", 5, java.time.LocalDate.of(2026, 4, 10));
        assertEquals("Dr Smith", result.getScheduleDoctorName());
        assertEquals(5, result.getScheduleDoctorId());
        assertEquals(java.time.LocalDate.of(2026, 4, 10), result.getScheduleDate());
    }
}
