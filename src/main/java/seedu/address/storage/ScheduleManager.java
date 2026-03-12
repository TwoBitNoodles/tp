package seedu.address.storage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScheduleManager {

    private static final String FILE_PATH = "data/schedule.json";

    public static Map<String, String> getScheduleIgnoreCase(String doctor, String date) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Map<String, Map<String, String>>> data =
                    mapper.readValue(new File(FILE_PATH), Map.class);

            for (String doctorName : data.keySet()) {

                if (doctorName.equalsIgnoreCase(doctor)) {

                    Map<String, Map<String, String>> doctorSchedule = data.get(doctorName);

                    return doctorSchedule.get(date);
                }
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
