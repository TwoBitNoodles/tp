package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

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
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.PatientBuilder;

public class AddApptCommandTest {
    private static final String SCHEDULE_FILE_PATH = "data/schedule.json";
    private static final String APPT_FILE_PATH = "data/appointments.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DOCTOR_NAME = "John Tan";
    private static final String PATIENT_NAME = "Jane Doe";

    private LocalDate date;

    @BeforeEach
    public void setup() throws Exception {
        date = LocalDate.now().plusDays(1);
        writeScheduleWithSlots(DOCTOR_NAME, date.toString(),
                Map.of("09:00", null, "09:30", null, "10:00", null));
        writeEmptyAppointments();
    }

    @Test
    public void execute_validAppointment_success() throws Exception {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName(DOCTOR_NAME).build();
        Patient patient = new PatientBuilder().withName(PATIENT_NAME).build();
        model.addDoctor(doctor);
        model.addPatient(patient);

        Appointment appt = new Appointment(DOCTOR_NAME, PATIENT_NAME, date.format(DATE_FORMAT), "09:30");
        AddApptCommand command = new AddApptCommand(appt);

        CommandResult result = command.execute(model);
        assertNotNull(result);
        assertNotNull(AppointmentManager.getAppointmentById(appt.getApptID()));
    }

    @Test
    public void execute_dateNotFound() throws CommandException {
        Model model = new ModelManager();
        Appointment appt = new Appointment(DOCTOR_NAME, PATIENT_NAME,
                date.plusDays(1).format(DATE_FORMAT), "10:00");
        AddApptCommand command = new AddApptCommand(appt);

        assertThrows(Exception.class, () -> command.execute(model));
    }

    private void writeScheduleWithSlots(String doctorName, String dateValue, Map<String, String> slots)
            throws Exception {
        Map<String, Map<String, Map<String, String>>> data = new LinkedHashMap<>();
        Map<String, Map<String, String>> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put(dateValue, new LinkedHashMap<>(slots));
        data.put(doctorName, doctorSchedule);

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
