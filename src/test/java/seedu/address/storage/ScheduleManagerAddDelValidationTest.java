package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.appointment.Appointment;

public class ScheduleManagerAddDelValidationTest {
    private static final String SCHEDULE_FILE_PATH = "data/schedule.json";

    private static final int DOCTOR_ID = 1;
    private static final String DOCTOR_NAME = "John Tan";
    private static final String PATIENT_NAME = "Jane Doe";
    private static final int PATIENT_ID = 1;

    private final ObjectMapper mapper = new ObjectMapper();
    private String date;
    private byte[] scheduleBackup;
    private boolean scheduleExisted;

    @BeforeEach
    public void setup() throws Exception {
        File scheduleFile = new File(SCHEDULE_FILE_PATH);
        scheduleExisted = scheduleFile.exists();
        scheduleBackup = scheduleExisted ? Files.readAllBytes(scheduleFile.toPath()) : null;

        date = LocalDate.now().plusDays(1).toString();
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("09:00", null);
        slots.put("09:30", null);
        slots.put("10:00", null);
        writeScheduleWithSlots(DOCTOR_ID, DOCTOR_NAME, date, slots);
    }

    @AfterEach
    public void cleanup() throws Exception {
        File scheduleFile = new File(SCHEDULE_FILE_PATH);
        if (scheduleExisted) {
            Files.write(scheduleFile.toPath(), scheduleBackup);
        } else {
            scheduleFile.delete();
        }
    }

    @Test
    public void addAppt_invalidDoctorId_throws() {
        Appointment appt = new Appointment(999, PATIENT_ID, date, "09:00");
        appt.setPatName(PATIENT_NAME);
        assertThrows(IOException.class, () -> ScheduleManager.addAppt(appt));
    }

    @Test
    public void addAppt_invalidTimeFormat_throws() {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date, "110:00");
        appt.setPatName(PATIENT_NAME);
        assertThrows(IOException.class, () -> ScheduleManager.addAppt(appt));
    }

    @Test
    public void addAppt_invalidDateFormat_throws() {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-13-01", "09:00");
        appt.setPatName(PATIENT_NAME);
        assertThrows(IOException.class, () -> ScheduleManager.addAppt(appt));
    }

    @Test
    public void addAppt_notThirtyMinuteSlot_throws() {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date, "09:15");
        appt.setPatName(PATIENT_NAME);
        assertThrows(IOException.class, () -> ScheduleManager.addAppt(appt));
    }

    @Test
    public void delAppt_noSuchAppointment_throws() {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date, "09:30");
        appt.setPatName(PATIENT_NAME);
        assertThrows(IOException.class, () -> ScheduleManager.delAppt(appt));
    }

    @Test
    public void addThenDelete_roundTripClearsSlot() throws Exception {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date, "09:30");
        appt.setPatName(PATIENT_NAME);
        ScheduleManager.addAppt(appt);
        assertEquals(PATIENT_NAME, ScheduleManager.getScheduleByDocId(DOCTOR_ID, date).get("09:30"));

        ScheduleManager.delAppt(appt);
        assertEquals(null, ScheduleManager.getScheduleByDocId(DOCTOR_ID, date).get("09:30"));
    }

    private void writeScheduleWithSlots(int doctorId, String doctorName, String dateValue, Map<String, String> slots)
            throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("__lastUpdated", LocalDate.now().toString());

        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put("docId", doctorId);
        doctorSchedule.put("doctorName", doctorName);
        doctorSchedule.put(dateValue, new LinkedHashMap<>(slots));
        data.put("doc_" + doctorId, doctorSchedule);

        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }
}
