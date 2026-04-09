package seedu.address.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Doctor;

/**
 * Manages access to the schedule data stored in the schedule JSON file.
 */
public class ScheduleManager {

    private static final String FILE_PATH = "data/schedule.json";
    private static final String LAST_UPDATED_KEY = "__lastUpdated";
    private static final String DOCTOR_NAME_KEY = "doctorName";
    private static final String DOC_ID_KEY = "docId";
    private static final String DOC_KEY_PREFIX = "doc_";
    private static final int SCHEDULE_WINDOW_DAYS = 7;
    private static final int SLOT_START_HOUR = 9;
    private static final int SLOT_END_HOUR = 17;
    private static final int SLOT_INTERVAL_MINUTES = 30;


    /**
     * Retrieves the schedule for a given doctor and date, ignoring case sensitivity of the doctor's name.
     *
     * @param doctor The name of the doctor whose schedule is to be retrieved.
     * @param date The date for which the schedule is to be retrieved, in the format "YYYY-MM-DD".
     * @return A map of time slots to patient names (or null if available), or null if the doctor is not found.
     * @throws IllegalArgumentException if the date is not found for the doctor.
     */
    public static Map<String, String> getScheduleIgnoreCase(String doctor, String date) {

        try {
            Map<String, Object> data = readScheduleFile();

            String matchedDoctor = findDoctorKey(data, doctor);
            if (matchedDoctor == null) {
                return null;
            }

            Map<String, Object> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
            if (!doctorSchedule.containsKey(date)) {
                throw new IllegalArgumentException("Date not found");
            }

            return getDateSlots(doctorSchedule, date);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the schedule for a given doctor id and date.
     *
     * @param docId The id of the doctor whose schedule is to be retrieved.
     * @param date The date for which the schedule is to be retrieved, in the format "YYYY-MM-DD".
     * @return A map of time slots to patient names (or null if available), or null if the doctor is not found.
     * @throws IllegalArgumentException if the date is not found for the doctor.
     */
    public static Map<String, String> getScheduleByDocId(int docId, String date) {
        try {
            Map<String, Object> data = readScheduleFile();
            String matchedDoctor = findDoctorKeyByDocId(data, docId);
            if (matchedDoctor == null) {
                return null;
            }

            Map<String, Object> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
            if (!doctorSchedule.containsKey(date)) {
                throw new IllegalArgumentException("Date not found");
            }

            return getDateSlots(doctorSchedule, date);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a new doctor to the schedule with default time slots for the next 7 days.
     */
    public static void addDoctorSchedule(Doctor doctor) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);

        file.getParentFile().mkdirs();
        if (!file.exists() || file.length() == 0) {
            mapper.writeValue(file, new HashMap<>());
        }

        LocalDate today = LocalDate.now();
        String doctorKey = doctor.getDocIdFromSchedule();

        Map<String, Object> data = readScheduleFile();
        rollScheduleForwardIfNeeded(data, today);

        if (data.containsKey(doctorKey)) {
            updateDoctorMetadata(data, doctorKey, doctor);
        } else {
            data.put(doctorKey, createDoctorSchedule(today, doctor));
        }

        data.put(LAST_UPDATED_KEY, today.toString());

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }

    /**
     * Removes a doctor's schedule entry from schedule.json.
     *
     * @param doctor the doctor whose schedule needs to be removed.
     */
    public static void removeDoctorSchedule(Doctor doctor) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        Map<String, Object> data = readScheduleFile();

        String doctorKey = doctor.getDocIdFromSchedule();
        if (data.containsKey(doctorKey)) {
            data.remove(doctorKey);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        }
    }

    /**
     * Renames a doctor's key in schedule.json, preserving all existing schedule data.
     *
     * @param doctor the doctor object with the updated name and existing id.
     */
    public static void renameDoctorSchedule(Doctor doctor) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        Map<String, Object> data = readScheduleFile();

        String doctorKey = doctor.getDocIdFromSchedule();
        if (data.containsKey(doctorKey)) {
            Map<String, Object> scheduleData = getDoctorSchedule(data, doctorKey);
            scheduleData.put(DOCTOR_NAME_KEY, doctor.getName().fullName);
            scheduleData.put(DOC_ID_KEY, doctor.getDocId());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        }
    }

    /**
     * Synchronises the schedule file so it is current for today.
     * Missing doctors from the provided list are added with empty 7-day windows.
     *
     * @param doctors doctors that should exist in the schedule file.
     */
    public static void syncSchedules(List<Doctor> doctors) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();

            Map<String, Object> data = readScheduleFile();
            LocalDate today = LocalDate.now();
            rollScheduleForwardIfNeeded(data, today);

            for (Doctor doctor : doctors) {
                String doctorKey = doctor.getDocIdFromSchedule();

                if (data.containsKey(doctorKey)) {
                    updateDoctorMetadata(data, doctorKey, doctor);
                } else {
                    data.put(doctorKey, createDoctorSchedule(today, doctor));
                }
            }

            data.put(LAST_UPDATED_KEY, today.toString());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Books an appointment for the specified doctor.
     *
     * @param appt the appointment to add.
     */
    public static void addAppt(Appointment appt) throws IOException {
        int doctorId = appt.getDocId();
        String patName = appt.getPatName();
        String date = appt.getDate();
        String time = appt.getTime();
        boolean found = false;

        if (!isValidDate(date)) {
            throw new IOException("Please input a valid date. The date must be formatted as YYYY-MM-DD");
        }
        //checks if date is within 7 days
        LocalDate apptDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        if (apptDate.isBefore(today) || apptDate.isAfter(sevenDaysLater)) {
            throw new IOException("Appointment date must be within 7 days from today!");
        }

        if (!isValidTime(time)) {
            throw new IOException("Please input a valid time. Time must be formatted as H:MM (e.g. 9:00 or 09:00)");
        }

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        Map<String, Object> data = readScheduleFile();

        String matchedDoctor = findDoctorKeyByDocId(data, doctorId);
        if (matchedDoctor != null) {
            Map<String, Object> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
            if (!doctorSchedule.containsKey(date)) {
                throw new IOException("Date not found!");
            }

            Map<String, String> slots = getDateSlots(doctorSchedule, date);
            TreeMap<String, Object> sortedSlots = new TreeMap<>(slots);

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm");
            DateTimeFormatter storageFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalTime apptTime = LocalTime.parse(time, inputFormatter);
            LocalTime firstTime = LocalTime.parse(sortedSlots.firstKey(), storageFormatter);
            LocalTime lastTime = LocalTime.parse(sortedSlots.lastKey(), storageFormatter);

            if (apptTime.isBefore(firstTime) || apptTime.isAfter(lastTime)) {
                throw new IOException("Please choose a time within operating hours");
            }

            // Formats input into JSON key format, to prevent dummy entries/overwrites.
            String standardizedTime = apptTime.format(storageFormatter);

            if (!slots.containsKey(standardizedTime)) {
                throw new IOException("The time " + time + " is not a valid 30-minute slot for this doctor.");
            }

            if (slots.get(standardizedTime) == null) {
                slots.put(standardizedTime, patName);
                found = true;
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            } else {
                throw new IOException("This slot is already booked. "
                        + "Please edit the appointment if you wish to change it");
            }

        } else {
            throw new IOException("Doctor not registered");

        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        System.out.println("sched added appt");

    }

    /**
     * Private helper method to validate the input date string
     * @param date
     * @return true if the date is correctly formatted, false if it is not
     */
    private static boolean isValidDate(String date) {
        try {
            LocalDate formattedDate = LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Private helper method to validate the time string
     * @param time
     * @return true if the time is formatted correctly
     */
    private static boolean isValidTime(String time) {
        try {
            LocalTime formattedDate = LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Deletes an appointment from a doctor's schedule at the given date and time.
     *
     * @param appt the appointment to delete.
     */
    public static void delAppt(Appointment appt) throws IOException {
        int doctorId = appt.getDocId();
        String pat = appt.getPatName();
        String date = appt.getDate();
        String time = appt.getTime();

        if (!isValidTime(time)) {
            throw new IOException("Please input a valid time. Time must be formatted as H:MM (e.g. 9:00 or 09:00)");
        }

        Map<String, Object> data = readScheduleFile();
        String matchedDoctor = findDoctorKeyByDocId(data, doctorId);

        if (matchedDoctor == null) {
            throw new IOException("Doctor not registered");
        }

        Map<String, Object> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
        if (!doctorSchedule.containsKey(date)) {
            throw new IOException("Date not found! Please choose a date within 7 days of today.");
        }

        Map<String, String> slots = getDateSlots(doctorSchedule, date);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm");
        DateTimeFormatter storageFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String standardizedTime;
        try {
            standardizedTime = LocalTime.parse(time, inputFormatter).format(storageFormatter);
        } catch (DateTimeParseException e) {
            throw new IOException("Please input a valid time. Time must be formatted as H:MM (e.g. 9:00 or 09:00)");
        }

        TreeMap<String, String> sortedSlots = new TreeMap<>(slots);
        LocalTime apptTime = LocalTime.parse(standardizedTime);
        if (apptTime.isBefore(LocalTime.parse(sortedSlots.firstKey()))
                || apptTime.isAfter(LocalTime.parse(sortedSlots.lastKey()))) {
            throw new IOException("Please choose a time within operating hours");
        }

        if (!slots.containsKey(standardizedTime)) {
            throw new IOException("There is no such time slot.");
        }

        String currentOccupant = slots.get(standardizedTime);
        if (currentOccupant == null || !currentOccupant.equalsIgnoreCase(pat)) {
            throw new IOException("No such appointment exists.");
        }

        slots.put(standardizedTime, null);
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);

    }

    // This method was assisted by Copilot as we ran into an IO Exception error in ModelManager's
    // deletePatientByAppt unexpectedly.
    /**
     * Removes a valid existing appointment as a helper.
     *
     * @param appt the appointment to remove.
     */
    public static void removeApptIfExists(Appointment appt) {
        try {
            String doctorName = appt.getDocName();
            String pat = appt.getPatName();
            String date = appt.getDate();
            String time = appt.getTime();

            Map<String, Object> data = readScheduleFile();
            String matchedDoctor = findDoctorKey(data, doctorName);

            if (matchedDoctor == null) {
                return;
            }

            Map<String, Object> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
            if (!doctorSchedule.containsKey(date)) {
                return;
            }

            Map<String, String> slots = getDateSlots(doctorSchedule, date);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String standardizedTime = LocalTime.parse(time,
                    DateTimeFormatter.ofPattern("H:mm")).format(formatter);

            if (!slots.containsKey(standardizedTime)) {
                return;
            }

            String currentOccupant = slots.get(standardizedTime);
            if (currentOccupant == null || !currentOccupant.equalsIgnoreCase(pat)) {
                return;
            }

            slots.put(standardizedTime, null);
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_PATH);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Warning: could not clean up schedule entry: " + e.getMessage());
        }
    }

    /**
     * Reads the schedule.json file to find the patient currently booked at a specific slot.
     */
    public static String getPatientAtSlot(String doctorName, String date, String time) throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        Map<String, Object> data = readScheduleFile();
        String matchedDoctor = findDoctorKey(data, doctorName);

        if (matchedDoctor == null) {
            return null;
        }

        Map<String, Object> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
        if (!doctorSchedule.containsKey(date)) {
            return null;
        }

        return getDateSlots(doctorSchedule, date).get(time);
    }

    /**
     * Reads the schedule.json file to find the patient currently booked at a specific slot by doctor id.
     */
    public static String getPatientAtSlotByDocId(int docId, String date, String time) throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        Map<String, Object> data = readScheduleFile();
        String matchedDoctor = findDoctorKeyByDocId(data, docId);

        if (matchedDoctor == null) {
            return null;
        }

        Map<String, Object> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
        if (!doctorSchedule.containsKey(date)) {
            return null;
        }

        return getDateSlots(doctorSchedule, date).get(time);
    }

    private static Map<String, Object> readScheduleFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new LinkedHashMap<>();
        }

        return mapper.readValue(file, LinkedHashMap.class);
    }

    private static String findDoctorKey(Map<String, Object> data, String doctorName) {
        for (String name : data.keySet()) {
            if (isMetadataKey(name)) {
                continue;
            }

            if (name.equalsIgnoreCase(doctorName)) {
                return name;
            }

            Object scheduleData = data.get(name);
            if (scheduleData instanceof Map<?, ?> scheduleMap) {
                Object storedName = scheduleMap.get(DOCTOR_NAME_KEY);
                if (storedName instanceof String && ((String) storedName).equalsIgnoreCase(doctorName)) {
                    return name;
                }
            }
        }

        return null;
    }

    private static String findDoctorKeyByDocId(Map<String, Object> data, int docId) {
        String exactKey = getDoctorKey(docId);
        if (data.containsKey(exactKey)) {
            return exactKey;
        }

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (isMetadataKey(entry.getKey())) {
                continue;
            }

            Object scheduleData = entry.getValue();
            if (scheduleData instanceof Map<?, ?> scheduleMap) {
                Object storedId = scheduleMap.get(DOC_ID_KEY);
                if (storedId instanceof Number && ((Number) storedId).intValue() == docId) {
                    return entry.getKey();
                }
                if (storedId instanceof String) {
                    try {
                        if (Integer.parseInt((String) storedId) == docId) {
                            return entry.getKey();
                        }
                    } catch (NumberFormatException e) {
                        // Ignore malformed ids and keep scanning.
                    }
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getDoctorSchedule(Map<String, Object> data, String doctorKey) {
        return (Map<String, Object>) data.get(doctorKey);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getDateSlots(Map<String, Object> doctorSchedule, String date) {
        return (Map<String, String>) doctorSchedule.get(date);
    }

    private static void rollScheduleForwardIfNeeded(Map<String, Object> data, LocalDate today) {
        LocalDate lastUpdated = inferLastUpdated(data);
        if (lastUpdated == null) {
            data.put(LAST_UPDATED_KEY, today.toString());
            return;
        }

        int daysToAdvance = (int) java.time.temporal.ChronoUnit.DAYS.between(lastUpdated, today);
        if (daysToAdvance <= 0) {
            data.put(LAST_UPDATED_KEY, lastUpdated.toString());
            return;
        }

        for (int i = 0; i < daysToAdvance; i++) {
            rollForwardOneDay(data);
        }

        data.put(LAST_UPDATED_KEY, today.toString());
    }

    private static void rollForwardOneDay(Map<String, Object> data) {
        for (String doctorKey : new ArrayList<>(data.keySet())) {
            if (isMetadataKey(doctorKey)) {
                continue;
            }

            Map<String, Object> doctorSchedule = getDoctorSchedule(data, doctorKey);
            String oldestDate = findBoundaryDate(doctorSchedule, true);
            String newestDate = findBoundaryDate(doctorSchedule, false);

            if (oldestDate != null) {
                doctorSchedule.remove(oldestDate);
            }

            if (newestDate != null) {
                LocalDate newDate = LocalDate.parse(newestDate).plusDays(1);
                doctorSchedule.put(newDate.toString(), createEmptySlots());
            }
        }
    }

    private static LocalDate inferLastUpdated(Map<String, Object> data) {
        Object storedLastUpdated = data.get(LAST_UPDATED_KEY);
        if (storedLastUpdated instanceof String) {
            try {
                return LocalDate.parse((String) storedLastUpdated);
            } catch (Exception e) {
                // Fall through and infer from the schedule contents.
            }
        }

        LocalDate earliestDate = null;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (isMetadataKey(entry.getKey())) {
                continue;
            }

            Map<String, Object> doctorSchedule = getDoctorSchedule(data, entry.getKey());
            String oldestDate = findBoundaryDate(doctorSchedule, true);
            if (oldestDate == null) {
                continue;
            }

            LocalDate candidate = LocalDate.parse(oldestDate);
            if (earliestDate == null || candidate.isBefore(earliestDate)) {
                earliestDate = candidate;
            }
        }

        return earliestDate;
    }

    private static String findBoundaryDate(Map<String, Object> doctorSchedule, boolean earliest) {
        String boundaryDate = null;
        LocalDate boundaryValue = null;

        for (String date : doctorSchedule.keySet()) {
            try {
                LocalDate current = LocalDate.parse(date);
                if (boundaryValue == null
                        || (earliest && current.isBefore(boundaryValue))
                        || (!earliest && current.isAfter(boundaryValue))) {
                    boundaryValue = current;
                    boundaryDate = date;
                }
            } catch (Exception e) {
                // Ignore malformed dates and keep scanning the rest of the schedule.
            }
        }

        return boundaryDate;
    }

    private static Map<String, Object> createDoctorSchedule(LocalDate startDate, Doctor doctor) {
        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put(DOC_ID_KEY, doctor.getDocId());
        doctorSchedule.put(DOCTOR_NAME_KEY, doctor.getName().fullName);

        for (int i = 0; i < SCHEDULE_WINDOW_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            doctorSchedule.put(date.toString(), createEmptySlots());
        }

        return doctorSchedule;
    }

    private static Map<String, Object> normalizeDoctorSchedule(Object scheduleData, Doctor doctor) {
        Map<String, Object> doctorSchedule = new LinkedHashMap<>();
        doctorSchedule.put(DOC_ID_KEY, doctor.getDocId());
        doctorSchedule.put(DOCTOR_NAME_KEY, doctor.getName().fullName);

        if (scheduleData instanceof Map<?, ?> scheduleMap) {
            for (Map.Entry<?, ?> entry : scheduleMap.entrySet()) {
                if (entry.getKey() instanceof String && !isMetadataKey((String) entry.getKey())) {
                    doctorSchedule.put((String) entry.getKey(), entry.getValue());
                }
            }
        }

        return doctorSchedule;
    }

    private static void updateDoctorMetadata(Map<String, Object> data, String doctorKey, Doctor doctor) {
        Map<String, Object> doctorSchedule = new LinkedHashMap<>(getDoctorSchedule(data, doctorKey));
        doctorSchedule.put(DOC_ID_KEY, doctor.getDocId());
        doctorSchedule.put(DOCTOR_NAME_KEY, doctor.getName().fullName);
        data.put(doctorKey, doctorSchedule);
    }

    private static String getDoctorKey(int docId) {
        return DOC_KEY_PREFIX + docId;
    }

    private static Map<String, String> createEmptySlots() {
        Map<String, String> slots = new LinkedHashMap<>();

        LocalTime time = LocalTime.of(SLOT_START_HOUR, 0);
        LocalTime end = LocalTime.of(SLOT_END_HOUR, 0);

        while (!time.isAfter(end.minusMinutes(SLOT_INTERVAL_MINUTES))) {
            slots.put(time.toString(), null);
            time = time.plusMinutes(SLOT_INTERVAL_MINUTES);
        }

        return slots;
    }

    private static boolean isMetadataKey(String key) {
        return LAST_UPDATED_KEY.equals(key) || DOCTOR_NAME_KEY.equals(key) || DOC_ID_KEY.equals(key);
    }
}
