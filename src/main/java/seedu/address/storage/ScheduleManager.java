package seedu.address.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.appointment.Appointment;

/**
 * Manages access to the schedule data stored in the schedule JSON file.
 */
public class ScheduleManager {

    private static final String FILE_PATH = "data/schedule.json";
    private static final String LAST_UPDATED_KEY = "__lastUpdated";

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

            Map<String, Map<String, String>> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
            if (!doctorSchedule.containsKey(date)) {
                throw new IllegalArgumentException("Date not found");
            }

            return doctorSchedule.get(date);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a new doctor to the schedule with default time slots for the next 7 days.
     */
    public static void addDoctorSchedule(String doctorName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();
            LocalDate today = LocalDate.now();

            Map<String, Object> data = readScheduleFile();
            rollScheduleForwardIfNeeded(data, today);

            if (findDoctorKey(data, doctorName) != null) {
                return;
            }

            data.put(doctorName, createDoctorSchedule(today));
            data.put(LAST_UPDATED_KEY, today.toString());

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Synchronises the schedule file so it is current for today.
     * Missing doctors from the provided list are added with empty 7-day windows.
     *
     * @param doctorNames doctors that should exist in the schedule file.
     */
    public static void syncSchedules(List<String> doctorNames) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();

            Map<String, Object> data = readScheduleFile();
            LocalDate today = LocalDate.now();
            rollScheduleForwardIfNeeded(data, today);

            for (String doctorName : doctorNames) {
                if (findDoctorKey(data, doctorName) == null) {
                    data.put(doctorName, createDoctorSchedule(today));
                }
            }

            data.put(LAST_UPDATED_KEY, today.toString());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * books an appointment for the specific doctor
     * @param appt
     */
    public static void addAppt(Appointment appt) throws IOException {
        String doctorName = appt.getDocName();
        String patName = appt.getPatName();
        String date = appt.getDate();
        String time = appt.getTime();

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        Map<String, Object> data = readScheduleFile();

        String matchedDoctor = findDoctorKey(data, doctorName);
        if (matchedDoctor != null) {
            Map<String, Map<String, String>> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
            if (!doctorSchedule.containsKey(date)) {
                throw new IOException("Date not found!");
            }

            Map<String, String> slots = doctorSchedule.get(date);
            slots.put(time, patName);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        }

        System.out.println("sched added appt");
    }

    /**
     * deletes an appointment according to the time and date from a doctor's schedule
     * @param appt
     */
    public static void delAppt(Appointment appt) {
        String doctorName = appt.getDocName();
        String date = appt.getDate();
        String time = appt.getTime();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_PATH);
            Map<String, Object> data = readScheduleFile();

            String matchedDoctor = findDoctorKey(data, doctorName);
            if (matchedDoctor != null) {
                Map<String, Map<String, String>> doctorSchedule = getDoctorSchedule(data, matchedDoctor);

                if (!doctorSchedule.containsKey(date)) {
                    throw new IOException("Date not found!");
                }

                Map<String, String> slots = doctorSchedule.get(date);
                slots.put(time, null);
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        Map<String, Map<String, String>> doctorSchedule = getDoctorSchedule(data, matchedDoctor);
        if (!doctorSchedule.containsKey(date)) {
            return null;
        }

        return doctorSchedule.get(date).get(time);
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
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, String>> getDoctorSchedule(Map<String, Object> data, String doctorKey) {
        return (Map<String, Map<String, String>>) data.get(doctorKey);
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

            Map<String, Map<String, String>> doctorSchedule = getDoctorSchedule(data, doctorKey);
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

            Map<String, Map<String, String>> doctorSchedule = getDoctorSchedule(data, entry.getKey());
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

    private static String findBoundaryDate(Map<String, Map<String, String>> doctorSchedule, boolean earliest) {
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

    private static Map<String, Map<String, String>> createDoctorSchedule(LocalDate startDate) {
        Map<String, Map<String, String>> doctorSchedule = new LinkedHashMap<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            doctorSchedule.put(date.toString(), createEmptySlots());
        }

        return doctorSchedule;
    }

    private static Map<String, String> createEmptySlots() {
        Map<String, String> slots = new LinkedHashMap<>();

        LocalTime time = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);

        while (!time.isAfter(end.minusMinutes(30))) {
            slots.put(time.toString(), null);
            time = time.plusMinutes(30);
        }

        return slots;
    }

    private static boolean isMetadataKey(String key) {
        return LAST_UPDATED_KEY.equals(key);
    }
}
