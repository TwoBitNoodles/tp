package seedu.address.model.appointment;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Creates an Appointment object
 */
public class Appointment {
    /**
     * Sentinel value for appointments that have not been persisted/assigned an ID yet.
     * ID assignment is handled by {@code seedu.address.storage.AppointmentManager}.
     */
    public static final int UNASSIGNED_ID = -1;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    private String patientName;
    private int patientId;
    private String doctorName;
    private int doctorId;
    private String date;
    private String time;
    private int apptID;

    /**
     * Creates an Appointment object with doctor and patient names.
     */
    public Appointment(String doctorName, String patientName, String date, String time) {
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.doctorId = UNASSIGNED_ID;
        this.patientId = UNASSIGNED_ID;
        this.apptID = UNASSIGNED_ID;
    }

    /**
     * Creates an Appointment with doctor and patient IDs (names can be resolved later).
     */
    public Appointment(int doctorId, int patientId, String date, String time) {
        this.doctorId = doctorId;
        this.doctorName = null;
        this.patientId = patientId;
        this.patientName = null;
        this.date = date;
        this.time = time;
        this.apptID = UNASSIGNED_ID;
    }

    /**
     * Creates an Appointment with names and an existing appointment ID.
     */
    public Appointment(String doctorName, String patientName, String date, String time, int apptID) {
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.doctorId = UNASSIGNED_ID;
        this.patientId = UNASSIGNED_ID;
        this.apptID = apptID;
    }

    /**
     * Creates an Appointment with all details including doctor/patient IDs and appointment ID.
     */
    public Appointment(int doctorId, String doctorName, int patientId, String patientName,
                       String date, String time, int apptID) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.apptID = apptID;
    }

    public String getPatName() {
        return patientName;
    }

    public void setPatName(String patientName) {
        this.patientName = patientName;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getDocName() {
        return doctorName;
    }

    public void setDocName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getDocId() {
        return doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getApptID() {
        return apptID;
    }

    public void setApptID(int apptID) {
        this.apptID = apptID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Appointment) {
            Appointment other = (Appointment) obj;

            LocalTime thisTime = parseTimeOrNull(this.time);
            LocalTime otherTime = parseTimeOrNull(other.getTime());
            boolean sameTime = (thisTime != null && otherTime != null)
                    ? thisTime.equals(otherTime)
                    : Objects.equals(this.time, other.getTime());

            return Objects.equals(this.patientName, other.getPatName())
                    && this.doctorId == other.getDocId()
                    && this.patientId == other.getPatientId()
                    && this.date.equals(other.getDate())
                    && sameTime;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientName, doctorId, patientId, date, time);
    }

    private static LocalTime parseTimeOrNull(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}
