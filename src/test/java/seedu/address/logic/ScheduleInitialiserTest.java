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

import seedu.address.model.ModelManager;

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

        ScheduleInitialiser.initialize(new ModelManager());

        Map<String, Object> data = mapper.readValue(new File(FILE_PATH), LinkedHashMap.class);
        assertEquals(today.toString(), data.get(LAST_UPDATED_KEY));

        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> johnTan = (Map<String, Map<String, String>>) data.get("John Tan");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> janeLim = (Map<String, Map<String, String>>) data.get("Jane Lim");

        assertFalse(johnTan.containsKey(today.minusDays(1).toString()));
        assertFalse(janeLim.containsKey(today.minusDays(1).toString()));
        assertTrue(johnTan.containsKey(today.plusDays(6).toString()));
        assertTrue(janeLim.containsKey(today.plusDays(6).toString()));

        assertEquals("Alice Lim", janeLim.get(today.toString()).get("10:00"));
    }

    @SuppressWarnings("unchecked")
    private void writeScheduleFile(LocalDate lastUpdatedDate) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put(LAST_UPDATED_KEY, lastUpdatedDate.toString());
        root.put("John Tan", createDoctorSchedule(lastUpdatedDate, null));
        root.put("Jane Lim", createDoctorSchedule(lastUpdatedDate, "Alice Lim"));

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }

    private Map<String, Map<String, String>> createDoctorSchedule(LocalDate startDate, String bookedPatient) {
        Map<String, Map<String, String>> doctorSchedule = new LinkedHashMap<>();

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
