package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.storage.AppointmentManager;
import seedu.address.storage.ScheduleManager;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.PatientBuilder;

public class EditApptCommandTest {
    private static final String SCHEDULE_FILE_PATH = "data/schedule.json";
    private static final String APPT_FILE_PATH = "data/appointments.json";
    private static final String DOCTOR_NAME = "John Tan";
    private static final int DOCTOR_ID = 1;
    private static final String PATIENT_NAME = "Jane Doe";
    private static final int PATIENT_ID = 1;
    private static final int APPT_ID = 1;

    private LocalDate date;
    private byte[] scheduleBackup;
    private byte[] apptBackup;
    private boolean scheduleExisted;
    private boolean apptExisted;

    @BeforeEach
    public void setup() throws Exception {
        File scheduleFile = new File(SCHEDULE_FILE_PATH);
        scheduleExisted = scheduleFile.exists();
        scheduleBackup = scheduleExisted ? Files.readAllBytes(scheduleFile.toPath()) : null;

        File apptFile = new File(APPT_FILE_PATH);
        apptExisted = apptFile.exists();
        apptBackup = apptExisted ? Files.readAllBytes(apptFile.toPath()) : null;

        date = LocalDate.now().plusDays(1);
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("09:00", null);
        slots.put("09:30", PATIENT_NAME);
        slots.put("10:00", null);
        writeScheduleWithSlots(DOCTOR_ID, DOCTOR_NAME, date.toString(), slots);
        writeAppointmentsWithId(APPT_ID, DOCTOR_ID, DOCTOR_NAME, PATIENT_ID, PATIENT_NAME, date.toString(), "09:30");
    }

    @AfterEach
    public void cleanup() throws Exception {
        File scheduleFile = new File(SCHEDULE_FILE_PATH);
        if (scheduleExisted) {
            Files.write(scheduleFile.toPath(), scheduleBackup);
        } else {
            scheduleFile.delete();
        }

        File apptFile = new File(APPT_FILE_PATH);
        if (apptExisted) {
            Files.write(apptFile.toPath(), apptBackup);
        } else {
            apptFile.delete();
        }
    }

    @Test
    public void execute_validId_success() throws Exception {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);
        patient.addAppt(new Appointment(DOCTOR_ID, DOCTOR_NAME, PATIENT_ID, PATIENT_NAME,
                date.toString(), "09:30", APPT_ID));

        EditApptCommand command = new EditApptCommand(APPT_ID, null, null, "10:00");
        command.execute(model);

        Appointment updated = AppointmentManager.getAppointmentById(APPT_ID);
        assertNotNull(updated);
        assertEquals("10:00", updated.getTime());
        assertEquals(1, patient.getApptList().size());
        assertEquals("10:00", patient.getApptList().get(0).getTime());

        Map<String, String> schedule = ScheduleManager.getScheduleIgnoreCase(DOCTOR_NAME, date.toString());
        assertEquals(null, schedule.get("09:30"));
        assertEquals(PATIENT_NAME, schedule.get("10:00"));
    }

    @Test
    public void execute_invalidTime_showsError() throws Exception {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);
        patient.addAppt(new Appointment(DOCTOR_ID, DOCTOR_NAME, PATIENT_ID, PATIENT_NAME,
                date.toString(), "09:30", APPT_ID));

        EditApptCommand command = new EditApptCommand(APPT_ID, null, null, "110:00");
        assertThrows(Exception.class, () -> command.execute(model));
    }

    @Test
    public void execute_invalidDoctorId_showsError() throws Exception {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);
        patient.addAppt(new Appointment(DOCTOR_ID, DOCTOR_NAME, PATIENT_ID, PATIENT_NAME,
                date.toString(), "09:30", APPT_ID));

        EditApptCommand command = new EditApptCommand(APPT_ID, "999", null, null);
        assertThrows(Exception.class, () -> command.execute(model));
    }

    @Test
    public void execute_invalidDate_showsError() throws Exception {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);
        patient.addAppt(new Appointment(DOCTOR_ID, DOCTOR_NAME, PATIENT_ID, PATIENT_NAME,
                date.toString(), "09:30", APPT_ID));

        EditApptCommand command = new EditApptCommand(APPT_ID, null, "2026-13-01", null);
        assertThrows(Exception.class, () -> command.execute(model));
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

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SCHEDULE_FILE_PATH);
        file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }

    private void writeAppointmentsWithId(int apptId, int doctorId, String doctorName, int patientId,
                                         String patientName,
                                         String dateValue, String timeValue) throws Exception {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("doctorId", doctorId);
        entry.put("doctorName", doctorName);
        entry.put("patientId", patientId);
        entry.put("patientName", patientName);
        entry.put("date", dateValue);
        entry.put("time", timeValue);
        data.put(String.valueOf(apptId), entry);

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(APPT_FILE_PATH);
        file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        AppointmentManager.initialise();
    }
}
