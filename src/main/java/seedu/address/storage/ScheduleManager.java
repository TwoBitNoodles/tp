package seedu.address.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manages access to the schedule data stored in the schedule JSON file.
 */
public class ScheduleManager {

    private static final String FILE_PATH = "data/schedule.json";

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
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_PATH);

            Map<String, Map<String, Map<String, String>>> data =
                    mapper.readValue(file, Map.class);

            // 🔍 find doctor (case-insensitive)
            Map<String, Map<String, String>> doctorSchedule = null;

            for (String doctorName : data.keySet()) {
                if (doctorName.equalsIgnoreCase(doctor)) {
                    doctorSchedule = data.get(doctorName);
                    break;
                }
            }

            if (doctorSchedule == null) {
                return null;
            }

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

            Map<String, Object> data = mapper.readValue(file, Map.class);

            if (data.containsKey(doctorName)) {
                return; // already exists
            }

            Map<String, Object> doctorSchedule = new HashMap<>();
            LocalDate today = LocalDate.now();

            for (int i = 0; i < 7; i++) {

                LocalDate date = today.plusDays(i);
                Map<String, String> slots = new LinkedHashMap<>();

                LocalTime time = LocalTime.of(9, 0);
                LocalTime end = LocalTime.of(17, 0);

                while (!time.isAfter(end.minusMinutes(30))) {
                    slots.put(time.toString(), null);
                    time = time.plusMinutes(30);
                }

                doctorSchedule.put(date.toString(), slots);
            }

            data.put(doctorName, doctorSchedule);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
