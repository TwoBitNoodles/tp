package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

// Currently only tests removeDoctorSchedule and renameDoctorSchedule methods
public class ScheduleManagerTest {

    private static final String SCHEDULE_FILE_PATH = "data/schedule.json";

    @Test
    public void removeDoctorSchedule_doctorExists_removesSuccessfully() throws Exception {
        ScheduleManager.addDoctorSchedule("Tony Stark");
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleBeforeRemoval.containsKey("Tony Stark"));
        ScheduleManager.removeDoctorSchedule("Tony Stark");
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertFalse(scheduleAfterRemoval.containsKey("Tony Stark"));
    }

    @Test
    public void removeDoctorSchedule_doctorDoesNotExist_noChange() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        int origsize = scheduleBeforeRemoval.size();
        ScheduleManager.removeDoctorSchedule("Doctor Strange");
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleAfterRemoval.size() == origsize);
    }

    @Test
    public void renameDoctorSchedule_doctorExists_renamesSuccessfully() throws Exception {
        ScheduleManager.addDoctorSchedule("Oldie");
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleBeforeRemoval.containsKey("Oldie"));
        ScheduleManager.renameDoctorSchedule("Oldie", "Goldie");
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertFalse(scheduleAfterRemoval.containsKey("Oldie"));
        assertTrue(scheduleAfterRemoval.containsKey("Goldie"));
        assertNotNull(scheduleAfterRemoval.get("Goldie"));
        ScheduleManager.removeDoctorSchedule("Goldie");
    }

    @Test
    public void renameDoctorSchedule_doctorDoesNotExist_noChange() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        int origsize = scheduleBeforeRemoval.size();
        ScheduleManager.renameDoctorSchedule("Sus", "Amogus");
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleAfterRemoval.size() == origsize);
        assertFalse(scheduleAfterRemoval.containsKey("Amogus"));
    }

}
