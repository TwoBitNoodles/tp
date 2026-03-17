package seedu.address.logic;

// 1. java.*
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 2. javax / org / external libs
import com.fasterxml.jackson.databind.ObjectMapper;

// 3. your project (seedu)
import seedu.address.model.Model;
import seedu.address.model.person.Doctor;

/**
 * Initializes the schedule JSON file with default schedules for all doctors in the address book.
 */
public class ScheduleInitialiser {

    private static final String FILE_PATH = "data/schedule.json";

    /**
     * Generates schedule file for all doctors if file does not exist.
     */
    public static void initialize(Model model) {

        File file = new File(FILE_PATH);

        if (file.exists()) {
            return; // don't overwrite existing data
        }

        Map<String, Object> scheduleData = new HashMap<>();

        LocalDate today = LocalDate.now();

        List<Doctor> doctors = model.getFilteredPersonList().stream()
                .filter(p -> p instanceof Doctor)
                .map(p -> (Doctor) p)
                .toList();

        for (Doctor doctor : doctors) {

            String doctorName = doctor.getName().fullName;

            Map<String, Object> doctorSchedule = new HashMap<>();

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

            scheduleData.put(doctorName, doctorSchedule);
        }

        writeToFile(scheduleData);
    }

    /*
     * Writes the schedule data to a JSON file.
     */
    private static void writeToFile(Map<String, Object> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File(FILE_PATH), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
