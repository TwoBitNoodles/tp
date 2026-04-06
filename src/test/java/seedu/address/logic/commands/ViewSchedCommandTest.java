package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.PatientBuilder;

/**
 * Tests for ViewSchedCommand.
 */
public class ViewSchedCommandTest {

    private static final String FILE_PATH = "data/schedule.json";

    private Model model;

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

        model = new ModelManager(new AddressBook(), new AddressBook(), new AddressBook(), new UserPrefs());
        model.addDoctor(new DoctorBuilder().withName("John Tan").build());
        model.addPatient(new PatientBuilder().withName("Jane Lim").build());
    }

    @AfterEach
    public void teardown() {
        new File(FILE_PATH).delete();
    }

    @Test
    public void execute_validDoctorAndDate_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(model);

        String expected = "Schedule for John Tan on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_caseInsensitiveDoctor_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("john tan", LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(model);

        String expected = "Schedule for john tan on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_doctorNotFound() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("Alice Lim", LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(model);

        assertEquals("Doctor not found.", result.getFeedbackToUser());
    }

    @Test
    public void execute_patientNameWithDate_failure() {
        ViewSchedCommand command =
                new ViewSchedCommand("Jane Lim", LocalDate.of(2026, 3, 20));

        assertCommandFailure(command, model,
                ViewSchedCommand.MESSAGE_INVALID_DOCTOR_NAME);
    }

    @Test
    public void execute_patientNameWeekly_failure() {
        ViewSchedCommand command = new ViewSchedCommand("Jane Lim", null);

        assertCommandFailure(command, model,
                ViewSchedCommand.MESSAGE_INVALID_DOCTOR_NAME);
    }

    @Test
    public void execute_dateNotAvailable() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", LocalDate.of(2026, 3, 25));

        CommandResult result = command.execute(model);

        assertEquals("No schedule available for this date.",
                     result.getFeedbackToUser());
    }
}
