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
import java.nio.file.Files;
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

    @Test
    public void getScheduleByDocId_missingDate_throwsIllegalArgumentException() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        String futureDate = today.plusDays(2).toString();

        assertThrows(IllegalArgumentException.class, () -> ScheduleManager.getScheduleByDocId(1, futureDate));
    }

    // test written by codex
    @Test
    public void addAppt_validAppointment_booksSlot() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today.plusDays(1), null);

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", today.plusDays(1).toString(), "09:00", -1);
        ScheduleManager.addAppt(appt);

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(1, today.plusDays(1).toString());
        assertEquals("Jane Lim", schedule.get("09:00"));
        assertEquals("Jane Lim", ScheduleManager.getPatientAtSlot("John Tan", today.plusDays(1).toString(), "09:00"));
    }

    @Test
    public void getPatientAtSlotByDocId_existingAppointment_returnsPatient() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, null);

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", futureDate.toString(), "09:00", -1);
        ScheduleManager.addAppt(appt);

        assertEquals("Jane Lim", ScheduleManager.getPatientAtSlotByDocId(1, futureDate.toString(), "09:00"));
    }

    @Test
    public void getPatientAtSlot_existingAppointment_returnsPatient() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, null);

        Appointment appt = new Appointment(1, "John Tan", 7, "Jane Lim", futureDate.toString(), "09:00", -1);
        ScheduleManager.addAppt(appt);

        assertEquals("Jane Lim", ScheduleManager.getPatientAtSlot("John Tan", futureDate.toString(), "09:00"));
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
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, "Jane Lim");

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", futureDate.toString(), "09:00", -1);
        ScheduleManager.delAppt(appt);

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(1, futureDate.toString());
        assertNull(schedule.get("09:00"));
    }

    @Test
    public void delAppt_wrongPatient_throwsIoException() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, "Alice Lim");

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", futureDate.toString(), "09:00", -1);
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

    @Test
    public void getPatientAtSlotByDocId_emptySlot_returnsNull() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, null);

        assertNull(ScheduleManager.getPatientAtSlotByDocId(1, futureDate.toString(), "09:00"));
    }

    @Test
    public void getPatientAtSlotByDocId_validSlot_returnsPatient() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, "Alice Lim");

        String patient = ScheduleManager.getPatientAtSlotByDocId(1, futureDate.toString(), "09:00");
        assertEquals("Alice Lim", patient);
    }

    @Test
    public void getPatientAtSlotByDocId_noFile_returnsNull() throws Exception {
        new File(SCHEDULE_FILE_PATH).delete();
        assertNull(ScheduleManager.getPatientAtSlotByDocId(1, LocalDate.now().plusDays(1).toString(), "09:00"));
    }


    @Test
    public void getPatientAtSlotByDocId_noDate_returnsNull() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, "Alice Lim");

        assertNull(ScheduleManager.getPatientAtSlotByDocId(1, futureDate.plusDays(5).toString(), "09:00"));
    }

    @Test
    public void getScheduleByDocId_noDate_throwsIllegalArgument() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, null);

        assertThrows(IllegalArgumentException.class, ()
                -> ScheduleManager.getScheduleByDocId(1, futureDate.plusDays(5).toString()));
    }

    @Test
    public void getPatientAtSlotByDocId_noDoctor_returnsNull() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, "Alice Lim");

        assertNull(ScheduleManager.getPatientAtSlotByDocId(99, futureDate.toString(), "09:00"));
    }

    @Test
    public void getScheduleIgnoreCase_noDate_throwsIllegalArgument() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), futureDate, null);

        assertThrows(IllegalArgumentException.class, ()
                -> ScheduleManager.getScheduleIgnoreCase("John Tan", futureDate.plusDays(5).toString()));
    }

    // added by copilot
    @Test
    public void addDoctorSchedule_noExistingFile_createsFileAndSchedule() throws Exception {
        new File(SCHEDULE_FILE_PATH).delete();
        Doctor doctor = new DoctorBuilder().withName("Dr John New").withDocId(10).build();

        ScheduleManager.addDoctorSchedule(doctor);

        File file = new File(SCHEDULE_FILE_PATH);
        assertTrue(file.exists());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = MAPPER.readValue(file, Map.class);
        assertTrue(data.containsKey("doc_10"));
    }

    // added by copilot
    @Test
    public void addDoctorSchedule_existingDoctor_updatesMetadata() throws Exception {
        Doctor doctor = new DoctorBuilder().withName("Old Name").withDocId(5).build();
        ScheduleManager.addDoctorSchedule(doctor);

        Doctor updated = new DoctorBuilder().withName("New Name").withDocId(5).build();
        ScheduleManager.addDoctorSchedule(updated);

        File file = new File(SCHEDULE_FILE_PATH);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = MAPPER.readValue(file, Map.class);
        assertTrue(data.containsKey("doc_5"));
        @SuppressWarnings("unchecked")
        Map<String, Object> docSchedule = (Map<String, Object>) data.get("doc_5");
        assertEquals("New Name", docSchedule.get("doctorName"));
    }

    // added by copilot
    @Test
    public void findDoctorKeyByDocId_docIdStoredAsString_findsDoctor() throws Exception {
        LocalDate today = LocalDate.now();
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put("docId", "7");
        doctorSchedule.put("doctorName", "Dr String");
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("09:00", null);
        doctorSchedule.put(today.toString(), slots);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("__lastUpdated", today.toString());
        root.put("legacy_key", doctorSchedule);
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, root);

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(7, today.toString());
        assertNotNull(schedule);
        assertTrue(schedule.containsKey("09:00"));
    }

    // added by copilot
    @Test
    public void findDoctorKeyByDocId_malformedStringId_skipsEntry() throws Exception {
        LocalDate today = LocalDate.now();
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put("docId", "not_a_number");
        doctorSchedule.put("doctorName", "Dr Malformed");
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("09:00", null);
        doctorSchedule.put(today.toString(), slots);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("__lastUpdated", today.toString());
        root.put("bad_key", doctorSchedule);
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, root);

        assertNull(ScheduleManager.getScheduleByDocId(1, today.toString()));
    }

    // added by copilot
    @Test
    public void findDoctorKey_legacyKeyAsName_findsDoctor() throws Exception {
        LocalDate today = LocalDate.now();
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();

        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put("docId", 1);
        doctorSchedule.put("doctorName", "Dr Legacy");
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("09:00", "Alice");
        doctorSchedule.put(today.toString(), slots);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("__lastUpdated", today.toString());
        root.put("Dr Legacy", doctorSchedule);
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, root);

        Map<String, String> schedule = ScheduleManager.getScheduleIgnoreCase("dr legacy", today.toString());
        assertNotNull(schedule);
        assertEquals("Alice", schedule.get("09:00"));
    }

    @Test
    public void findDoctorKey_nonExistentName_returnsNull() throws Exception {
        LocalDate today = LocalDate.now();
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        assertNull(ScheduleManager.getScheduleIgnoreCase("John Cena", today.toString()));
    }

    // added by copilot
    @Test
    public void getScheduleByDocId_corruptedFile_returnsNull() throws Exception {
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), "not valid json at all");

        assertNull(ScheduleManager.getScheduleByDocId(1, LocalDate.now().toString()));
    }

    @Test
    public void addAppt_duplicateAppointmentSamePatient_throwsException() throws Exception {
        //written by copilot
        LocalDate today = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        ScheduleManager.addAppt(appt);

        Appointment duplicateAppt = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        IOException thrown = assertThrows(IOException.class, () -> ScheduleManager.addAppt(duplicateAppt));
        assertEquals("This appointment already exists", thrown.getMessage());
    }

    @Test
    public void addAppt_differentPatientSameSlot_throws() throws Exception {
        //written by copilot
        LocalDate today = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        Appointment appt1 = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        ScheduleManager.addAppt(appt1);

        Appointment appt2 = new Appointment(1, "John Tan", 3, "Bob Smith", today.toString(), "09:00", -1);
        IOException thrown = assertThrows(IOException.class, () -> ScheduleManager.addAppt(appt2));
        assertTrue(thrown.getMessage().contains("already booked"));
    }

    @Test
    public void getPatientAtSlotByDocId_afterAddingAppointment_returnsCorrectPatient() throws Exception {
        //written by copilot
        LocalDate today = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        Appointment appt = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        ScheduleManager.addAppt(appt);

        String patient = ScheduleManager.getPatientAtSlotByDocId(1, today.toString(), "09:00");
        assertEquals("Jane Lim", patient);
    }

    @Test
    public void getScheduleByDocId_afterAddingMultipleAppointments_maintainsConsistency() throws Exception {
        //written by copilot
        LocalDate today = LocalDate.now().plusDays(1);
        writeScheduleFile(createDoctor(1, "John Tan"), today, null);

        Appointment appt1 = new Appointment(1, "John Tan", 2, "Jane Lim", today.toString(), "09:00", -1);
        ScheduleManager.addAppt(appt1);
        Appointment appt2 = new Appointment(1, "John Tan", 3, "Bob Smith", today.toString(), "09:30", -1);
        ScheduleManager.addAppt(appt2);

        Map<String, String> schedule = ScheduleManager.getScheduleByDocId(1, today.toString());
        assertEquals("Jane Lim", schedule.get("09:00"));
        assertEquals("Bob Smith", schedule.get("09:30"));
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
