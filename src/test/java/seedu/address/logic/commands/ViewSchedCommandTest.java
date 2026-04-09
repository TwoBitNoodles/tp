package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 3, 20);

    private Model model;

    @BeforeEach
    public void setup() throws Exception {
        writeScheduleFile();

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

    // test written by codex
    private void writeScheduleFile() throws Exception {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("doc_1", createDoctorSchedule(1, "John Tan", "Alice Lim"));
        root.put("doc_2", createDoctorSchedule(2, "John Tan", null));

        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(file, root);
    }

    // test written by codex
    private Map<String, Object> createDoctorSchedule(int docId, String doctorName, String bookedPatient)
            throws Exception {
        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put("docId", docId);
        doctorSchedule.put("doctorName", doctorName);

        Map<String, String> fixedDaySlots = new LinkedHashMap<>();
        fixedDaySlots.put("09:00", null);
        fixedDaySlots.put("10:00", bookedPatient);
        doctorSchedule.put(FIXED_DATE.toString(), fixedDaySlots);

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            Map<String, String> slots = new LinkedHashMap<>();
            slots.put("09:00", null);
            slots.put("09:30", null);
            slots.put("10:00", bookedPatient != null && i == 0 ? bookedPatient : null);
            doctorSchedule.put(date.toString(), slots);
        }

        return doctorSchedule;
    }

    @Test
    public void execute_validDoctorAndDate_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", 1, FIXED_DATE);

        CommandResult result = command.execute(model);

        String expected = "Schedule for John Tan (ID: 1) on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_caseInsensitiveDoctor_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("john tan", 1, FIXED_DATE);

        CommandResult result = command.execute(model);

        String expected = "Schedule for john tan (ID: 1) on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_doctorNotFound() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", 99, FIXED_DATE);

        CommandResult result = command.execute(model);

        assertEquals("Doctor not found.", result.getFeedbackToUser());
    }

    @Test
    public void execute_sameNameDifferentId_success() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("John Tan", 2, FIXED_DATE);

        CommandResult result = command.execute(model);

        String expected = "Schedule for John Tan (ID: 2) on 2026-03-20\n\n";

        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_patientNameWithDate_failure() throws Exception {
        ViewSchedCommand command =
                new ViewSchedCommand("Jane Lim", 1, FIXED_DATE);

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

    // test written by codex
    @Test
    public void execute_weeklySchedule_success() throws Exception {
        ViewSchedCommand command = new ViewSchedCommand("John Tan", 1, null);

        CommandResult result = command.execute(model);

        assertTrue(result.isWeekly());
        assertEquals("Weekly schedule for John Tan (ID: 1)", result.getFeedbackToUser());
        assertEquals(7, result.getWeeklySchedule().size());
        assertTrue(result.getWeeklySchedule().containsKey(LocalDate.now().toString()));
        assertTrue(result.getWeeklySchedule().containsKey(LocalDate.now().plusDays(6).toString()));
        assertEquals("Alice Lim",
                result.getWeeklySchedule().get(LocalDate.now().toString()).get("10:00"));
    }
}
