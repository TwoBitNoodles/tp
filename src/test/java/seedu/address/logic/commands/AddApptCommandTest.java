package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.storage.AppointmentManager;
import seedu.address.storage.ScheduleManager;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.PatientBuilder;

public class AddApptCommandTest {
    private static final String SCHEDULE_FILE_PATH = "data/schedule.json";
    private static final String APPT_FILE_PATH = "data/appointments.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DOCTOR_NAME = "John Tan";
    private static final int DOCTOR_ID = 1;
    private static final String PATIENT_NAME = "Jane Doe";
    private static final int PATIENT_ID = 1;

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
        slots.put("09:30", null);
        slots.put("10:00", null);
        writeScheduleWithSlots(DOCTOR_ID, DOCTOR_NAME, date.toString(), slots);
        writeEmptyAppointments();
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
    public void execute_validAppointment_success() throws Exception {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date.format(DATE_FORMAT), "10:00");
        AddApptCommand command = new AddApptCommand(appt);

        CommandResult result = command.execute(model);
        assertNotNull(result);
        assertNotNull(AppointmentManager.getAppointmentById(appt.getApptID()));
        assertEquals(1, patient.getApptList().size());
    }

    @Test
    public void execute_invalidDoctorId_throws() {
        Model model = new ModelManager();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addPatient(patient);

        Appointment appt = new Appointment(999, PATIENT_ID, date.format(DATE_FORMAT), "09:30");
        AddApptCommand command = new AddApptCommand(appt);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_invalidTime_throws() {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date.format(DATE_FORMAT), "110:00");
        AddApptCommand command = new AddApptCommand(appt);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_invalidDate_throws() {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-13-01", "10:00");
        AddApptCommand command = new AddApptCommand(appt);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_dateOutOfRange_throws() {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        String outOfRangeDate = LocalDate.now().plusDays(8).format(DATE_FORMAT);
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, outOfRangeDate, "09:30");
        AddApptCommand command = new AddApptCommand(appt);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_dateNotFound() throws CommandException {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID,
                date.plusDays(1).format(DATE_FORMAT), "10:00");
        AddApptCommand command = new AddApptCommand(appt);

        assertThrows(Exception.class, () -> command.execute(model));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-05-01", "09:00");
        AddApptCommand command = new AddApptCommand(appt);
        assertTrue(command.equals(command));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        Appointment appt1 = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-05-01", "09:00");
        Appointment appt2 = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-05-01", "09:00");
        AddApptCommand command1 = new AddApptCommand(appt1);
        AddApptCommand command2 = new AddApptCommand(appt2);
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-05-01", "09:00");
        AddApptCommand command = new AddApptCommand(appt);
        assertFalse(command.equals("string"));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        Appointment appt1 = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-05-01", "09:00");
        Appointment appt2 = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-05-01", "10:00");
        AddApptCommand command1 = new AddApptCommand(appt1);
        AddApptCommand command2 = new AddApptCommand(appt2);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void toStringMethod() {
        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, "2026-05-01", "09:00");
        AddApptCommand command = new AddApptCommand(appt);
        String result = command.toString();
        assertTrue(result.contains("toAdd"));
    }

    @Test
    public void execute_apptManagerFails_rollbackAndThrows() throws Exception {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date.format(DATE_FORMAT), "09:00");
        AddApptCommand command = new AddApptCommand(appt);

        // Corrupt appointments file so AppointmentManager.addAppointment fails with IOException
        File apptFile = new File(APPT_FILE_PATH);
        Files.write(apptFile.toPath(), "CORRUPT".getBytes());

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_patientMultipleDoctorsSameSlot_throws() throws Exception {
        //written by copilot
        Model model = new ModelManager();
        Doctor doctor1 = new DoctorBuilder().withName("Doctor One").withDocId(DOCTOR_ID).build();
        Doctor doctor2 = new DoctorBuilder().withName("Doctor Two").withDocId(2)
                .withPhone("99999999").withEmail("doctortwo@example.com").build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor1);
        model.addDoctor(doctor2);
        model.addPatient(patient);

        ScheduleManager.addDoctorSchedule(doctor1);
        ScheduleManager.addDoctorSchedule(doctor2);

        Appointment appt1 = new Appointment(DOCTOR_ID, PATIENT_ID, date.format(DATE_FORMAT), "10:00");
        AddApptCommand command1 = new AddApptCommand(appt1);
        command1.execute(model);

        Appointment appt2 = new Appointment(2, PATIENT_ID, date.format(DATE_FORMAT), "10:00");
        AddApptCommand command2 = new AddApptCommand(appt2);

        assertThrows(CommandException.class, () -> command2.execute(model));
    }

    @Test
    public void execute_duplicateAppointment_throws() throws Exception {
        //written by copilot
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).withDocId(DOCTOR_ID).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).withPatId(PATIENT_ID).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        Appointment appt = new Appointment(DOCTOR_ID, PATIENT_ID, date.format(DATE_FORMAT), "10:00");
        AddApptCommand command1 = new AddApptCommand(appt);
        command1.execute(model);

        Appointment appt2 = new Appointment(DOCTOR_ID, PATIENT_ID, date.format(DATE_FORMAT), "10:00");
        AddApptCommand command2 = new AddApptCommand(appt2);

        assertThrows(CommandException.class, () -> command2.execute(model));
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

    private void writeEmptyAppointments() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(APPT_FILE_PATH);
        file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, new LinkedHashMap<>());
        AppointmentManager.initialise();
    }
}
