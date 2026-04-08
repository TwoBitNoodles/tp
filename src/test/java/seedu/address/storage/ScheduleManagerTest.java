package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.person.Doctor;
import seedu.address.testutil.DoctorBuilder;

// Currently only tests removeDoctorSchedule and renameDoctorSchedule methods
public class ScheduleManagerTest {

    private static final String SCHEDULE_FILE_PATH = "data/schedule.json";

    @Test
    public void syncSchedules_sameNameDoctors_keepsBothEntries() throws Exception {
        Doctor doctorOne = new DoctorBuilder().withName("Tony Stark").withPhone("11111111")
                .withEmail("tony1@avengers.com").withDocId(1).build();
        Doctor doctorTwo = new DoctorBuilder().withName("Tony Stark").withPhone("22222222")
                .withEmail("tony2@avengers.com").withDocId(2).build();

        ScheduleManager.syncSchedules(List.of(doctorOne, doctorTwo));

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleData = mapper.readValue(file, Map.class);

        assertTrue(scheduleData.containsKey("doc_1"));
        assertTrue(scheduleData.containsKey("doc_2"));

        @SuppressWarnings("unchecked")
        Map<String, Object> doctorOneSchedule = (Map<String, Object>) scheduleData.get("doc_1");
        @SuppressWarnings("unchecked")
        Map<String, Object> doctorTwoSchedule = (Map<String, Object>) scheduleData.get("doc_2");

        assertEquals(1, doctorOneSchedule.get("docId"));
        assertEquals(2, doctorTwoSchedule.get("docId"));
        assertEquals("Tony Stark", doctorOneSchedule.get("doctorName"));
        assertEquals("Tony Stark", doctorTwoSchedule.get("doctorName"));

        File scheduleFile = new File(SCHEDULE_FILE_PATH);
        assertTrue(scheduleFile.exists());
        scheduleFile.delete();
    }

    @Test
    public void removeDoctorSchedule_doctorExists_removesSuccessfully() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Tony Stark").withDocId(1).build();
        ScheduleManager.addDoctorSchedule(doctor);
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleBeforeRemoval.containsKey("doc_1"));
        ScheduleManager.removeDoctorSchedule(doctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertFalse(scheduleAfterRemoval.containsKey("doc_1"));
    }

    @Test
    public void removeDoctorSchedule_doctorDoesNotExist_noChange() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Doctor Strange").withDocId(99).build();
        ensureEmptyScheduleFile();
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        int origsize = scheduleBeforeRemoval.size();
        ScheduleManager.removeDoctorSchedule(doctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleAfterRemoval.size() == origsize);
    }

    @Test
    public void renameDoctorSchedule_doctorExists_renamesSuccessfully() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Oldie").withDocId(1).build();
        ScheduleManager.addDoctorSchedule(doctor);
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleBeforeRemoval.containsKey("doc_1"));
        Doctor renamedDoctor = new DoctorBuilder(doctor).withName("Goldie").build();
        ScheduleManager.renameDoctorSchedule(renamedDoctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertFalse(scheduleAfterRemoval.containsKey("Oldie"));
        assertTrue(scheduleAfterRemoval.containsKey("doc_1"));
        assertNotNull(scheduleAfterRemoval.get("doc_1"));
        ScheduleManager.removeDoctorSchedule(renamedDoctor);
    }

    @Test
    public void renameDoctorSchedule_doctorDoesNotExist_noChange() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Sus").withDocId(99999).build();
        ensureEmptyScheduleFile();
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = mapper.readValue(file, Map.class);
        int origsize = scheduleBeforeRemoval.size();
        ScheduleManager.renameDoctorSchedule(doctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = mapper.readValue(file, Map.class);
        assertTrue(scheduleAfterRemoval.size() == origsize);
        assertFalse(scheduleAfterRemoval.containsKey("Amogus"));
    }

    private void ensureEmptyScheduleFile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> root = new java.util.LinkedHashMap<>();
        root.put("__lastUpdated", "2026-04-08");
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }

}
