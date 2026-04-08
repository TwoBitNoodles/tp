package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                + "  \"doc_1\": {\n"
                + "    \"docId\": 1,\n"
                + "    \"doctorName\": \"John Tan\",\n"
                + "    \"2026-03-20\": {\n"
                + "      \"09:00\": null,\n"
                + "      \"10:00\": \"Alice Lim\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"doc_2\": {\n"
                + "    \"docId\": 2,\n"
                + "    \"doctorName\": \"John Tan\",\n"
                + "    \"2026-03-20\": {\n"
                + "      \"09:00\": null,\n"
                + "      \"10:00\": null\n"
                + "    }\n"
                + "  }\n"
                + "}");
        writer.close();

        model = new ModelManager(new AddressBook(), new AddressBook(), new AddressBook(), new UserPrefs());
        model.addDoctor(new DoctorBuilder().withName("John Tan").withPhone("11111111")
                .withEmail("john1@doc.com").withDocId(1).build());
        model.addDoctor(new DoctorBuilder().withName("John Tan").withPhone("22222222")
                .withEmail("john2@doc.com").withDocId(2).build());
        model.addPatient(new PatientBuilder().withName("Jane Lim").build());
    }

    @AfterEach
    public void teardown() {
        new File(FILE_PATH).delete();
    }

    @Test
    public void execute_validDoctorAndDate_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", 1, LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(model);

        String expected = "Schedule for John Tan (ID: 1) on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_caseInsensitiveDoctor_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("john tan", 1, LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(model);

        String expected = "Schedule for john tan (ID: 1) on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_doctorNotFound() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", 99, LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(model);

        assertEquals("Doctor not found.", result.getFeedbackToUser());
    }

    @Test
    public void execute_sameNameDifferentId_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", 2, LocalDate.of(2026, 3, 20));

        CommandResult result = command.execute(model);

        String expected = "Schedule for John Tan (ID: 2) on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_patientNameWithDate_failure() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("Jane Lim", 1, LocalDate.of(2026, 3, 20));

        assertEquals(ViewSchedCommand.MESSAGE_DOCTOR_NOT_FOUND,
                command.execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_patientNameWeekly_failure() throws Exception {
        ViewSchedCommand command = new ViewSchedCommand("Jane Lim", 1, null);

        assertEquals(ViewSchedCommand.MESSAGE_DOCTOR_NOT_FOUND,
                command.execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_dateNotAvailable() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", 1, LocalDate.of(2026, 3, 25));

        CommandResult result = command.execute(model);

        assertEquals("No schedule available for this date.",
                     result.getFeedbackToUser());
    }
}
