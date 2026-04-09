package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
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
import seedu.address.testutil.DoctorBuilder;

/**
 * Tests for {@link ScheduleInitialiser}.
 */
public class ScheduleInitialiserTest {

    private static final String FILE_PATH = "data/schedule.json";
    private static final String LAST_UPDATED_KEY = "__lastUpdated";

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() throws IOException {
        writeScheduleFile(LocalDate.now().minusDays(1));
    }

    @AfterEach
    public void teardown() {
        new File(FILE_PATH).delete();
    }

    @Test
    public void initialize_staleSchedule_rollsForwardOneDay() throws Exception {
        LocalDate today = LocalDate.now();

        AddressBook doctorBook = new AddressBook();
        doctorBook.addDoctor(new DoctorBuilder().withName("John Tan").withPhone("11111111")
                .withEmail("john1@doc.com").withDocId(1).build());
        doctorBook.addDoctor(new DoctorBuilder().withName("John Tan").withPhone("22222222")
                .withEmail("john2@doc.com").withDocId(2).build());

        Model model = new ModelManager(doctorBook, new AddressBook(), new AddressBook(),
                new seedu.address.model.UserPrefs());

        ScheduleInitialiser.initialize(model);

        Map<String, Object> data = mapper.readValue(new File(FILE_PATH), LinkedHashMap.class);
        assertEquals(today.toString(), data.get(LAST_UPDATED_KEY));

        @SuppressWarnings("unchecked")
        Map<String, Object> doctorOne = (Map<String, Object>) data.get("doc_1");
        @SuppressWarnings("unchecked")
        Map<String, Object> doctorTwo = (Map<String, Object>) data.get("doc_2");

        assertTrue(doctorOne.containsKey(today.toString()));
        assertTrue(doctorTwo.containsKey(today.toString()));
        assertFalse(doctorOne.containsKey(today.minusDays(1).toString()));
        assertFalse(doctorTwo.containsKey(today.minusDays(1).toString()));
        assertEquals(1, doctorOne.get("docId"));
        assertEquals(2, doctorTwo.get("docId"));
    }

    // test written by codex
    @Test
    public void initialize_currentSchedule_keepsExistingDates() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(today);

        AddressBook doctorBook = new AddressBook();
        doctorBook.addDoctor(new DoctorBuilder().withName("John Tan").withPhone("11111111")
                .withEmail("john1@doc.com").withDocId(1).build());

        Model model = new ModelManager(doctorBook, new AddressBook(), new AddressBook(),
                new seedu.address.model.UserPrefs());

        ScheduleInitialiser.initialize(model);

        Map<String, Object> data = mapper.readValue(new File(FILE_PATH), LinkedHashMap.class);
        assertEquals(today.toString(), data.get(LAST_UPDATED_KEY));

        @SuppressWarnings("unchecked")
        Map<String, Object> doctorOne = (Map<String, Object>) data.get("doc_1");
        @SuppressWarnings("unchecked")
        Map<String, Object> doctorTwo = (Map<String, Object>) data.get("doc_2");
        assertTrue(doctorOne.containsKey(today.toString()));
        assertTrue(doctorOne.containsKey(today.plusDays(6).toString()));
        assertEquals("Alice Lim", ((Map<String, String>) doctorTwo.get(today.plusDays(1).toString())).get("10:00"));
    }

    @SuppressWarnings("unchecked")
    private void writeScheduleFile(LocalDate lastUpdatedDate) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put(LAST_UPDATED_KEY, lastUpdatedDate.toString());
        root.put("doc_1", createDoctorSchedule(lastUpdatedDate, 1, "John Tan", null));
        root.put("doc_2", createDoctorSchedule(lastUpdatedDate, 2, "John Tan", "Alice Lim"));

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }

    private Map<String, Object> createDoctorSchedule(LocalDate startDate, int docId,
            String doctorName, String bookedPatient) {
        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put("docId", docId);
        doctorSchedule.put("doctorName", doctorName);

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, String> slots = new LinkedHashMap<>();
            slots.put("09:00", null);
            slots.put("09:30", null);
            slots.put("10:00", null);
            if (bookedPatient != null && i == 1) {
                slots.put("10:00", bookedPatient);
            }
            doctorSchedule.put(date.toString(), slots);
        }

        return doctorSchedule;
    }
}
