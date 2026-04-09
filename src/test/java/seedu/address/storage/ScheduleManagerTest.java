package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Doctor;
import seedu.address.testutil.DoctorBuilder;

public class ScheduleManagerTest {

    private static final String SCHEDULE_FILE_PATH = "data/schedule.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @AfterEach
    public void tearDown() {
        new File(SCHEDULE_FILE_PATH).delete();
    }

    @Test
    public void syncSchedules_sameNameDoctors_keepsBothEntries() throws Exception {
        Doctor doctorOne = new DoctorBuilder().withName("Tony Stark").withPhone("11111111")
                .withEmail("tony1@avengers.com").withDocId(1).build();
        Doctor doctorTwo = new DoctorBuilder().withName("Tony Stark").withPhone("22222222")
                .withEmail("tony2@avengers.com").withDocId(2).build();

        ScheduleManager.syncSchedules(List.of(doctorOne, doctorTwo));

        File file = new File(SCHEDULE_FILE_PATH);
        assertTrue(file.exists());

        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleData = MAPPER.readValue(file, Map.class);

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
    }

    @Test
    public void getScheduleByDocId_existingDoctor_returnsSlots() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, "Alice Lim");

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(1, today.toString());

        assertNotNull(schedule);
        assertEquals(3, schedule.size());
        assertTrue(schedule.containsKey("09:00"));
        assertEquals("Alice Lim", schedule.get("09:00"));
    }

    // test written by codex
    @Test
    public void getScheduleIgnoreCase_existingDoctor_returnsSlots() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        Map<String, String> schedule = ScheduleManager.getScheduleIgnoreCase("john tan", today.toString());

        assertNotNull(schedule);
        assertNull(schedule.get("10:00"));
    }

    @Test
    public void getScheduleByDocId_missingDoctor_returnsNull() throws Exception {
        writeScheduleFile(createDoctor(1, "John Tan"), LocalDate.now(), null);

        assertNull(ScheduleManager.getScheduleByDocId(99, LocalDate.now().toString()));
    }

    // test written by codex
    @Test
    public void addAppt_validAppointment_booksSlot() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        ScheduleManager.addAppt(appt);

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(1, today.toString());
        assertEquals("Jane Lim", schedule.get("09:00"));
        assertEquals("Jane Lim", ScheduleManager.getPatientAtSlot("John Tan", today.toString(), "09:00"));
    }

    @Test
    public void addAppt_invalidDate_throwsIoException() {
        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", LocalDate.now().minusDays(1).toString(),
                "09:00", -1);

        IOException thrown = assertThrows(IOException.class, () -> ScheduleManager.addAppt(appt));
        assertEquals("Appointment date must be within 7 days from today!", thrown.getMessage());
    }

    // test written by codex
    @Test
    public void delAppt_validAppointment_clearsSlot() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, "Jane Lim");

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        ScheduleManager.delAppt(appt);

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(1, today.toString());
        assertNull(schedule.get("09:00"));
    }

    @Test
    public void delAppt_wrongPatient_throwsIoException() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, "Alice Lim");

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        IOException thrown = assertThrows(IOException.class, () -> ScheduleManager.delAppt(appt));
        assertEquals("No such appointment exists.", thrown.getMessage());
    }

    // test written by codex
    @Test
    public void removeApptIfExists_missingDoctor_noThrow() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, "Alice Lim");

        Appointment appt = new Appointment(999, "Unknown Doctor", 2, "Jane Lim", today.toString(), "09:00", -1);
        assertDoesNotThrow(() -> ScheduleManager.removeApptIfExists(appt));

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(1, today.toString());
        assertEquals("Alice Lim", schedule.get("09:00"));
    }

    @Test
    public void removeDoctorSchedule_doctorExists_removesSuccessfully() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Tony Stark").withDocId(1).build();
        ScheduleManager.addDoctorSchedule(doctor);
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = MAPPER.readValue(file, Map.class);
        assertTrue(scheduleBeforeRemoval.containsKey("doc_1"));
        ScheduleManager.removeDoctorSchedule(doctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = MAPPER.readValue(file, Map.class);
        assertFalse(scheduleAfterRemoval.containsKey("doc_1"));
    }

    @Test
    public void removeDoctorSchedule_doctorDoesNotExist_noChange() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Doctor Strange").withDocId(99).build();
        ensureEmptyScheduleFile();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = MAPPER.readValue(file, Map.class);
        int origSize = scheduleBeforeRemoval.size();
        ScheduleManager.removeDoctorSchedule(doctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = MAPPER.readValue(file, Map.class);
        assertTrue(scheduleAfterRemoval.size() == origSize);
    }

    @Test
    public void renameDoctorSchedule_doctorExists_renamesSuccessfully() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Oldie").withDocId(1).build();
        ScheduleManager.addDoctorSchedule(doctor);
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = MAPPER.readValue(file, Map.class);
        assertTrue(scheduleBeforeRemoval.containsKey("doc_1"));
        Doctor renamedDoctor = new DoctorBuilder(doctor).withName("Goldie").build();
        ScheduleManager.renameDoctorSchedule(renamedDoctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = MAPPER.readValue(file, Map.class);
        assertFalse(scheduleAfterRemoval.containsKey("Oldie"));
        assertTrue(scheduleAfterRemoval.containsKey("doc_1"));
        assertNotNull(scheduleAfterRemoval.get("doc_1"));
        ScheduleManager.removeDoctorSchedule(renamedDoctor);
    }

    @Test
    public void renameDoctorSchedule_doctorDoesNotExist_noChange() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Sus").withDocId(99999).build();
        ensureEmptyScheduleFile();
        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleBeforeRemoval = MAPPER.readValue(file, Map.class);
        int origSize = scheduleBeforeRemoval.size();
        ScheduleManager.renameDoctorSchedule(doctor);
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleAfterRemoval = MAPPER.readValue(file, Map.class);
        assertTrue(scheduleAfterRemoval.size() == origSize);
        assertFalse(scheduleAfterRemoval.containsKey("Amogus"));
    }

    private void writeScheduleFile(Doctor doctor, LocalDate date, String bookedPatient) throws Exception {
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("__lastUpdated", date.toString());
        root.put(doctor.getDocIdFromSchedule(), createDoctorSchedule(doctor, date, bookedPatient));

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }

    private Map<String, Object> createDoctorSchedule(Doctor doctor, LocalDate date, String bookedPatient) {
        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put("docId", doctor.getDocId());
        doctorSchedule.put("doctorName", doctor.getName().fullName);

        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("09:00", bookedPatient);
        slots.put("09:30", null);
        slots.put("10:00", null);
        doctorSchedule.put(date.toString(), slots);

        return doctorSchedule;
    }

    private Doctor createDoctor(int docId, String name) {
        return new DoctorBuilder().withName(name).withPhone("11111111")
                .withEmail("doc" + docId + "@example.com").withDocId(docId).build();
    }

    private void ensureEmptyScheduleFile() throws Exception {
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("__lastUpdated", LocalDate.now().toString());
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }
}
