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
     * Initialises an Appointment object with the doctorname, patient name, and the date and time
     * @param doctorName
     * @param patientName
     * @param date
     * @param time
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
     * Initialises an Appointment using a doctor id and patient id.
     * The doctor/patient names can be resolved from the model when needed.
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
     * initialises an appointment with the ID inputted by the user
     * @param doctorName
     * @param patientName
     * @param date
     * @param time
     * @param apptID
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
     * Initialises an appointment with explicit doctor/patient ids + appointment id.
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
        return this.patientName;
    }

    public void setPatName(String patientName) {
        this.patientName = patientName;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getDocName() {
        return this.doctorName;
    }

    public void setDocName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getDocId() {
        return doctorId;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public int getApptID() {
        return this.apptID;
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

            return this.patientName.equals(other.getPatName())
                    && this.doctorId == other.getDocId()
                    && this.patientId == other.getPatientId()
                    && this.date.equals(other.getDate())
                    && sameTime;
        } else {
            return false;
        }
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
