package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for ViewSchedCommand.
 */
public class ViewSchedCommandTest {

    private static final String FILE_PATH = "data/schedule.json";

    @BeforeEach
    public void setup() throws Exception {
        // Create test JSON file
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        FileWriter writer = new FileWriter(file);

        writer.write("{\n"
                + "  \"John Tan\": {\n"
                + "    \"2026-03-20\": {\n"
                + "      \"09:00\": null,\n"
                + "      \"10:00\": \"Alice Lim\"\n"
                + "    }\n"
                + "  }\n"
                + "}");

        writer.close();
    }

    @Test
    public void execute_validDoctorAndDate_success() {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(null);

        String expected =
                "Schedule for John Tan on 2026-03-20\n\n"
                        + "09:00 – Available\n"
                        + "10:00 – Booked\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_caseInsensitiveDoctor_success() {
        ViewSchedCommand command =
                new ViewSchedCommand("john tan", LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(null);

        String expected =
                "Schedule for john tan on 2026-03-20\n\n"
                        + "09:00 – Available\n"
                        + "10:00 – Booked\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_doctorNotFound() {
        ViewSchedCommand command =
                new ViewSchedCommand("Alice Lim", LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(null);

        assertEquals("Doctor not found.", result.getFeedbackToUser());
    }

    @Test
    public void execute_dateNotAvailable() {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", LocalDate.of(2026, 3, 25));

        CommandResult result = command.execute(null);

        assertEquals("No schedule available for this date.",
                result.getFeedbackToUser());
    }
}
