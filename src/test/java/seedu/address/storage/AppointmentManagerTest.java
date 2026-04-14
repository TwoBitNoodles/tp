// Used copilot to generate some of the test cases, and modified them to fit our codebase
package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.appointment.Appointment;

public class AppointmentManagerTest {
    private static final String APPT_FILE_PATH = "data/appointments.json";
    private static final int PATIENT_ID = 3;
    private static final String PATIENT_NAME = "Alice Patient";

    private final ObjectMapper mapper = new ObjectMapper();
    private String date;
    private byte[] apptBackup;
    private boolean apptExisted;

    @BeforeEach
    public void setup() throws Exception {
        File apptFile = new File(APPT_FILE_PATH);
        apptExisted = apptFile.exists();
        apptBackup = apptExisted ? Files.readAllBytes(apptFile.toPath()) : null;

        date = LocalDate.now().plusDays(1).toString();
        writeEmptyAppointments();
    }

    @AfterEach
    public void cleanup() throws Exception {
        File apptFile = new File(APPT_FILE_PATH);
        if (apptExisted) {
            Files.write(apptFile.toPath(), apptBackup);
        } else {
            apptFile.delete();
        }
    }

    @Test
    public void addAppointment_normalizesTimeAndPersistsDoctorInfo() throws Exception {
        Appointment appt = new Appointment(1, PATIENT_ID, date, "9:00");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);

        int id = AppointmentManager.addAppointment(appt);

        Appointment stored = AppointmentManager.getAppointmentById(id);
        assertNotNull(stored);
        assertEquals(1, stored.getDocId());
        assertEquals("Dr Bob", stored.getDocName());
        assertEquals(PATIENT_ID, stored.getPatientId());
        assertEquals(PATIENT_NAME, stored.getPatName());
        assertEquals(date, stored.getDate());
        assertEquals("09:00", stored.getTime());
    }

    @Test
    public void findPatientIdBySlot_existingSlot_returnsPatientId() throws Exception {
        Appointment appt = new Appointment(7, PATIENT_ID, date, "09:30");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        AppointmentManager.addAppointment(appt);

        assertEquals(Integer.valueOf(PATIENT_ID), AppointmentManager.findPatientIdBySlot(7, date, "9:30"));
        assertEquals(Integer.valueOf(PATIENT_ID), AppointmentManager.findPatientIdBySlot(7, date, "09:30"));
    }

    @Test
    public void findPatientIdBySlot_noMatch_returnsNull() throws Exception {
        assertNull(AppointmentManager.findPatientIdBySlot(999, date, "09:00"));
    }

    @Test
    public void findAppointmentIdBySlot_acceptsDifferentTimeFormats() throws Exception {
        Appointment appt = new Appointment(7, PATIENT_ID, date, "09:30");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        int id = AppointmentManager.addAppointment(appt);

        assertEquals(Integer.valueOf(id), AppointmentManager.findAppointmentIdBySlot(7, date, "9:30"));
        assertEquals(Integer.valueOf(id), AppointmentManager.findAppointmentIdBySlot(7, date, "09:30"));
    }

    @Test
    public void updateAppointment_updatesStoredRecord() throws Exception {
        Appointment appt = new Appointment(1, PATIENT_ID, date, "9:00");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        int id = AppointmentManager.addAppointment(appt);

        Appointment updated = new Appointment(2, "Dr Charlie", PATIENT_ID, PATIENT_NAME, date, "10:00", id);
        AppointmentManager.updateAppointment(id, updated);

        Appointment stored = AppointmentManager.getAppointmentById(id);
        assertNotNull(stored);
        assertEquals(2, stored.getDocId());
        assertEquals("Dr Charlie", stored.getDocName());
        assertEquals(PATIENT_ID, stored.getPatientId());
        assertEquals("10:00", stored.getTime());
    }

    @Test
    public void deleteAppointment_invalidId_throws() throws Exception {
        assertThrows(IOException.class, () -> AppointmentManager.deleteAppointment(999));
        assertNull(AppointmentManager.getAppointmentById(999));
    }

    @Test
    public void deleteAppointmentsByDoctorId_removesMatchingAppointments() throws Exception {
        Appointment appt1 = new Appointment(5, PATIENT_ID, date, "9:00");
        appt1.setDocName("Dr Bob");
        appt1.setPatName(PATIENT_NAME);
        int id1 = AppointmentManager.addAppointment(appt1);

        Appointment appt2 = new Appointment(5, PATIENT_ID, date, "10:00");
        appt2.setDocName("Dr Bob");
        appt2.setPatName(PATIENT_NAME);
        int id2 = AppointmentManager.addAppointment(appt2);

        Appointment appt3 = new Appointment(6, PATIENT_ID, date, "11:00");
        appt3.setDocName("Dr Charlie");
        appt3.setPatName(PATIENT_NAME);
        int id3 = AppointmentManager.addAppointment(appt3);

        AppointmentManager.deleteAppointmentsByDoctorId(5);

        assertNull(AppointmentManager.getAppointmentById(id1));
        assertNull(AppointmentManager.getAppointmentById(id2));
        assertNotNull(AppointmentManager.getAppointmentById(id3));
    }

    @Test
    public void deleteAppointmentsByDoctorId_noMatches_noChange() throws Exception {
        Appointment appt = new Appointment(7, PATIENT_ID, date, "9:00");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        int id = AppointmentManager.addAppointment(appt);

        AppointmentManager.deleteAppointmentsByDoctorId(999);

        assertNotNull(AppointmentManager.getAppointmentById(id));
    }

    @Test
    public void updateDoctorNameInAppointments_updatesMatchingRecords() throws Exception {
        Appointment appt1 = new Appointment(1, PATIENT_ID, date, "9:00");
        appt1.setDocName("Dr Bob");
        appt1.setPatName(PATIENT_NAME);
        int id1 = AppointmentManager.addAppointment(appt1);

        Appointment appt2 = new Appointment(2, PATIENT_ID, date, "10:00");
        appt2.setDocName("Dr Charlie");
        appt2.setPatName(PATIENT_NAME);
        int id2 = AppointmentManager.addAppointment(appt2);

        AppointmentManager.updateDoctorNameInAppointments(1, "Dr Stark");

        Appointment stored1 = AppointmentManager.getAppointmentById(id1);
        assertEquals("Dr Stark", stored1.getDocName());

        Appointment stored2 = AppointmentManager.getAppointmentById(id2);
        assertEquals("Dr Charlie", stored2.getDocName());
    }

    @Test
    public void updateDoctorNameInAppointments_noMatches_noChange() throws Exception {
        Appointment appt = new Appointment(1, PATIENT_ID, date, "9:00");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        int id = AppointmentManager.addAppointment(appt);

        AppointmentManager.updateDoctorNameInAppointments(999, "Dr New");

        Appointment stored = AppointmentManager.getAppointmentById(id);
        assertEquals("Dr Bob", stored.getDocName());
    }

    @Test
    public void deleteAppointmentsByPatientId_removesMatchingAppointments() throws Exception {
        Appointment appt1 = new Appointment(1, PATIENT_ID, date, "9:00");
        appt1.setDocName("Dr Bob");
        appt1.setPatName(PATIENT_NAME);
        int id1 = AppointmentManager.addAppointment(appt1);

        Appointment appt2 = new Appointment(1, PATIENT_ID, date, "10:00");
        appt2.setDocName("Dr Bob");
        appt2.setPatName(PATIENT_NAME);
        int id2 = AppointmentManager.addAppointment(appt2);

        Appointment appt3 = new Appointment(2, 5, date, "11:00");
        appt3.setDocName("Dr Charlie");
        appt3.setPatName("Bob Patient");
        int id3 = AppointmentManager.addAppointment(appt3);

        AppointmentManager.deleteAppointmentsByPatientId(PATIENT_ID);

        assertNull(AppointmentManager.getAppointmentById(id1));
        assertNull(AppointmentManager.getAppointmentById(id2));
        assertNotNull(AppointmentManager.getAppointmentById(id3));
    }

    @Test
    public void deleteAppointmentsByPatientId_noMatches_noChange() throws Exception {
        Appointment appt = new Appointment(1, PATIENT_ID, date, "9:00");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        int id = AppointmentManager.addAppointment(appt);

        AppointmentManager.deleteAppointmentsByPatientId(999);

        assertNotNull(AppointmentManager.getAppointmentById(id));
    }

    @Test
    public void updatePatientNameInAppointments_updatesMatchingRecords() throws Exception {
        Appointment appt1 = new Appointment(1, PATIENT_ID, date, "9:00");
        appt1.setDocName("Dr Bob");
        appt1.setPatName(PATIENT_NAME);
        int id1 = AppointmentManager.addAppointment(appt1);

        Appointment appt2 = new Appointment(2, 5, date, "10:00");
        appt2.setDocName("Dr Charlie");
        appt2.setPatName("Bob Patient");
        int id2 = AppointmentManager.addAppointment(appt2);

        AppointmentManager.updatePatientNameInAppointments(PATIENT_ID, "New Alice");

        Appointment stored1 = AppointmentManager.getAppointmentById(id1);
        assertEquals("New Alice", stored1.getPatName());

        Appointment stored2 = AppointmentManager.getAppointmentById(id2);
        assertEquals("Bob Patient", stored2.getPatName());
    }

    @Test
    public void updatePatientNameInAppointments_noMatches_noChange() throws Exception {
        Appointment appt = new Appointment(1, PATIENT_ID, date, "9:00");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        int id = AppointmentManager.addAppointment(appt);

        AppointmentManager.updatePatientNameInAppointments(999, "New Name");

        Appointment stored = AppointmentManager.getAppointmentById(id);
        assertEquals(PATIENT_NAME, stored.getPatName());
    }

    @Test
    public void findAppointmentIdBySlot_noMatch_returnsNull() throws Exception {
        assertNull(AppointmentManager.findAppointmentIdBySlot(999, date, "09:00"));
    }

    @Test
    public void updateAppointment_invalidId_throws() throws Exception {
        Appointment updated = new Appointment(2,
            "Dr Charlie", PATIENT_ID, PATIENT_NAME, date, "10:00", 999);
        assertThrows(IOException.class, () -> AppointmentManager.updateAppointment(999, updated));
    }

    @Test
    public void getAppointmentById_nonExistent_returnsNull() throws Exception {
        assertNull(AppointmentManager.getAppointmentById(9999));
    }

    @Test
    public void deleteAppointment_validId_succeeds() throws Exception {
        Appointment appt = new Appointment(1, PATIENT_ID, date, "9:00");
        appt.setDocName("Dr Bob");
        appt.setPatName(PATIENT_NAME);
        int id = AppointmentManager.addAppointment(appt);

        AppointmentManager.deleteAppointment(id);

        assertNull(AppointmentManager.getAppointmentById(id));
    }

    @Test
    public void addAppointment_withoutDoctorId_createsAppointment() throws Exception {
        Appointment appt = new Appointment(Appointment.UNASSIGNED_ID,
            "Dr Bob", PATIENT_ID, PATIENT_NAME, date, "9:00", Appointment.UNASSIGNED_ID);

        int id = AppointmentManager.addAppointment(appt);

        Appointment stored = AppointmentManager.getAppointmentById(id);
        assertNotNull(stored);
        assertEquals(Appointment.UNASSIGNED_ID, stored.getDocId());
        assertEquals("Dr Bob", stored.getDocName());
        assertEquals(PATIENT_ID, stored.getPatientId());
    }

    @Test
    public void addAppointment_withoutPatientId_createsAppointment() throws Exception {
        Appointment appt = new Appointment(1, "Dr Bob",
            Appointment.UNASSIGNED_ID, PATIENT_NAME, date, "9:00", Appointment.UNASSIGNED_ID);

        int id = AppointmentManager.addAppointment(appt);

        Appointment stored = AppointmentManager.getAppointmentById(id);
        assertNotNull(stored);
        assertEquals(1, stored.getDocId());
        assertEquals("Dr Bob", stored.getDocName());
        assertEquals(Appointment.UNASSIGNED_ID, stored.getPatientId());
    }

    @Test
    public void findPatientIdBySlot_withoutDoctorId_returnsNull() throws Exception {
        Appointment appt = new Appointment(Appointment.UNASSIGNED_ID, "Dr Bob",
            PATIENT_ID, PATIENT_NAME, date, "9:00", Appointment.UNASSIGNED_ID);
        AppointmentManager.addAppointment(appt);

        assertNull(AppointmentManager.findPatientIdBySlot(7, date, "9:00"));
    }

    @Test
    public void findAppointmentIdBySlot_withoutDoctorId_returnsNull() throws Exception {
        Appointment appt = new Appointment("Dr Bob", PATIENT_NAME, date, "9:00");
        AppointmentManager.addAppointment(appt);

        assertNull(AppointmentManager.findAppointmentIdBySlot(999, date, "9:00"));
    }

    private void writeEmptyAppointments() throws Exception {
        File file = new File(APPT_FILE_PATH);
        file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, new LinkedHashMap<>());
        AppointmentManager.initialise();
    }
}
