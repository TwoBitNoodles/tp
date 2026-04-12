package seedu.address.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
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
     * Adds a new appointment and persists it to the appointments file.
     * @param appt the appointment to add
     * @return the appointment ID assigned
     * @throws IOException if file cannot be accessed
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
     * Retrieves an appointment by its ID.
     * @param apptId the appointment ID
     * @return the appointment, or null if not found
     * @throws IOException if file cannot be accessed
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
     * Finds the appointment ID for a given doctor's time slot.
     * @param doctorId the doctor's ID
     * @param date the appointment date
     * @param time the appointment time
     * @return the appointment ID, or null if not found
     * @throws IOException if file cannot be accessed
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
     * Finds the patient ID for a given doctor's time slot.
     * @param doctorId the doctor's ID
     * @param date the appointment date
     * @param time the appointment time
     * @return the patient ID, or null if not found
     * @throws IOException if file cannot be accessed
     */
    public static Integer findPatientIdBySlot(int doctorId, String date, String time) throws IOException {
        String normalizedTime = normalizeTime(time);
        Map<String, AppointmentData> data = readAppointments();
        for (AppointmentData record : data.values()) {
            if (record == null || record.doctorId == null || record.patientId == null) {
                continue;
            }
            if (record.doctorId == doctorId
                    && Objects.equals(record.date, date)
                    && Objects.equals(record.time, normalizedTime)) {
                return record.patientId;
            }
        }
        return null;
    }

    /**
     * Updates an existing appointment with new details.
     * @param apptId the ID of the appointment to update
     * @param appt the new appointment details
     * @throws IOException if file cannot be accessed or appointment not found
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
     * Deletes an appointment from the appointments file.
     * @param apptId the ID of the appointment to delete
     * @throws IOException if file cannot be accessed or appointment not found
     */
    public static void deleteAppointment(int apptId) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        if (data.remove(String.valueOf(apptId)) == null) {
            throw new IOException("Appointment id not found: " + apptId);
        }

        writeAppointments(data);
    }

    /**
     * Deletes all appointments associated with the given doctor ID.
     * @param doctorId the doctor's ID
     * @throws IOException if file cannot be accessed
     */
    public static void deleteAppointmentsByDoctorId(int doctorId) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        data.values().removeIf(record -> record.doctorId != null && record.doctorId == doctorId);
        writeAppointments(data);
    }

    /**
     * Deletes all appointments associated with the given patient ID.
     * @param patientId the patient's ID
     * @throws IOException if file cannot be accessed
     */
    public static void deleteAppointmentsByPatientId(int patientId) {
        try {
            Map<String, AppointmentData> data = readAppointments();
            List<AppointmentData> lst = data.values().stream()
                .filter(record -> record.patientId != null && record.patientId.equals(patientId))
                .toList();

            for (AppointmentData record : lst) {
                ScheduleManager.removeApptIfExists(new Appointment(record.doctorName, record.patientName,
                    record.date, record.time));
            }

            data.values().removeIf(record -> record.patientId != null && record.patientId == patientId);
            writeAppointments(data);
        } catch (IOException e) {
            System.err.println("Error deleting appointments for patient ID " + patientId + ": " + e.getMessage());
        }

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

    /**
     * Data transfer object for serializing appointments to JSON.
     */
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

    /**
     * Updates all appointments with the old doctor name to use the new doctor name.
     * @param oldName the previous doctor name
     * @param newName the new doctor name
     * @throws IOException
     */
    public static void updateDoctorNameInAppointments(String oldName, String newName) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        boolean updated = false;

        for (AppointmentData apptData : data.values()) {
            if (apptData.doctorName != null && apptData.doctorName.equalsIgnoreCase(oldName)) {
                apptData.doctorName = newName;
                updated = true;
            }
        }

        if (updated) {
            writeAppointments(data);
        }
    }

    /**
     * Updates all appointments with the old patient name to use the new patient name.
     * @param oldName the previous patient name
     * @param newName the new patient name
     * @throws IOException
     */
    public static void updatePatientNameInAppointments(String oldName, String newName) throws IOException {
        Map<String, AppointmentData> data = readAppointments();
        boolean updated = false;

        for (AppointmentData apptData : data.values()) {
            if (apptData.patientName != null && apptData.patientName.equalsIgnoreCase(oldName)) {
                apptData.patientName = newName;
                updated = true;
            }
        }

        if (updated) {
            writeAppointments(data);
        }
    }
}
