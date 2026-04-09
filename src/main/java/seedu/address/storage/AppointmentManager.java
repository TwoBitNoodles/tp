package seedu.address.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.appointment.Appointment;

/**
 * Manages the storage of Appointments in the appointments JSON file for easy editing of the schedule
 */
public class AppointmentManager {
    private static final String FILE_PATH = "data/appointments.json";
    // Use field visibility so AppointmentData can stay as a private DTO without getters/setters.
    private static final ObjectMapper mapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    private static final DateTimeFormatter INPUT_TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter STORAGE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    static {
        initialise();
    }

    /**
     * Initialises the appointments JSON file
     */
    public static void initialise() {
        try {
            File file = new File(FILE_PATH);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists() || file.length() == 0) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, new LinkedHashMap<String, AppointmentData>());
            }
        } catch (IOException e) {
            System.err.println("Could not create appointments.json: " + e.getMessage());
        }
    }

    /**
     * Adds appointment to the JSON file with the id and all the info
     * @param appt
     * @return
     * @throws IOException
     */
    public static int addAppointment(Appointment appt) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        int nextId = data.keySet().stream()
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(-1) + 1;

        data.put(String.valueOf(nextId),
                new AppointmentData(appt.getDocId() != Appointment.UNASSIGNED_ID ? appt.getDocId() : null,
                        appt.getDocName(),
                        appt.getPatientId() != Appointment.UNASSIGNED_ID ? appt.getPatientId() : null,
                        appt.getPatName(),
                        appt.getDate(), normalizeTime(appt.getTime())));
        writeAppointments(data);
        appt.setApptID(nextId);
        return nextId;
    }

    /**
     * Retrieves the appointment info using its id number
     * @param apptId
     * @return
     * @throws IOException
     */
    public static Appointment getAppointmentById(int apptId) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        AppointmentData record = data.get(String.valueOf(apptId));
        if (record == null) {
            return null;
        }

        return new Appointment(record.doctorId != null ? record.doctorId : Appointment.UNASSIGNED_ID,
                record.doctorName,
                record.patientId != null ? record.patientId : Appointment.UNASSIGNED_ID,
                record.patientName,
                record.date, record.time, apptId);
    }

    /**
     * Finds the appointment id for a given schedule slot.
     *
     * @return appointment id, or {@code null} if not found.
     */
    public static Integer findAppointmentIdBySlot(int doctorId, String date, String time) throws IOException {
        String normalizedTime = normalizeTime(time);
        Map<String, AppointmentData> data = readAppointments();
        for (Map.Entry<String, AppointmentData> entry : data.entrySet()) {
            AppointmentData record = entry.getValue();
            if (record == null || record.doctorId == null) {
                continue;
            }
            if (record.doctorId == doctorId
                    && Objects.equals(record.date, date)
                    && Objects.equals(record.time, normalizedTime)) {
                return Integer.parseInt(entry.getKey());
            }
        }
        return null;
    }

    /**
     * updates appointment by retrieving th eappointment by ID, then updating its info
     * @param apptId
     * @param appt
     * @throws IOException
     */
    public static void updateAppointment(int apptId, Appointment appt) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        String key = String.valueOf(apptId);
        if (!data.containsKey(key)) {
            throw new IOException("Appointment id not found: " + apptId);
        }

        data.put(key, new AppointmentData(appt.getDocId() != Appointment.UNASSIGNED_ID ? appt.getDocId() : null,
                appt.getDocName(),
                appt.getPatientId() != Appointment.UNASSIGNED_ID ? appt.getPatientId() : null,
                appt.getPatName(),
                appt.getDate(), normalizeTime(appt.getTime())));
        writeAppointments(data);
    }

    /**
     * deletes the appointment from the JSON file
     * @param apptId
     * @throws IOException
     */
    public static void deleteAppointment(int apptId) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        if (data.remove(String.valueOf(apptId)) == null) {
            throw new IOException("Appointment id not found: " + apptId);
        }

        writeAppointments(data);
    }

    private static Map<String, AppointmentData> readAppointments() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new LinkedHashMap<>();
        }

        return mapper.readValue(file, new TypeReference<LinkedHashMap<String, AppointmentData>>() {});
    }

    private static void writeAppointments(Map<String, AppointmentData> data) throws IOException {
        File file = new File(FILE_PATH);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }

    private static final class AppointmentData {
        private Integer doctorId;
        private String doctorName;
        private Integer patientId;
        private String patientName;
        private String date;
        private String time;

        public AppointmentData() {}

        public AppointmentData(Integer doctorId, String doctorName, Integer patientId, String patientName,
                               String date, String time) {
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.patientId = patientId;
            this.patientName = patientName;
            this.date = date;
            this.time = time;
        }
    }

    private static String normalizeTime(String time) throws IOException {
        try {
            return LocalTime.parse(time, INPUT_TIME_FORMAT).format(STORAGE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IOException("Please input a valid time. Time must be formatted as H:MM (e.g. 9:00 or 09:00)");
        }
    }
}
