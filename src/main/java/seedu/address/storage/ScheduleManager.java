package seedu.address.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.model.appointment.Appointment;

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

            file.getParentFile().mkdirs();
            if (!file.exists() || file.length() == 0) {
                mapper.writeValue(file, new HashMap<>());
            }

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

    /**
     * books an appointment for the specific doctor
     * @param appt
     */
    public static void addAppt(Appointment appt) throws IOException {
        String doctorName = appt.getDocName();
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
            throw new IOException("Please input a valid time. Time must be formatted as HH:MM");
        }

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        Map<String, Object> data = mapper.readValue(file, Map.class);

        for (String name : data.keySet()) {
            if (name.equalsIgnoreCase(doctorName) || name.toLowerCase().contains(doctorName.toLowerCase())) {
                Map<String, Object> doctorSchedule =
                        (Map<String, Object>) data.get(name);
                if (!doctorSchedule.containsKey(date)) {
                    throw new IOException("Date not found!");
                }

                Map<String, Object> slots = (Map<String, Object>) doctorSchedule.get(date);
                TreeMap<String, Object> sortedSlots = new TreeMap<>(slots);

                LocalTime apptTime = LocalTime.parse(time);
                LocalTime firstTime = LocalTime.parse(sortedSlots.firstKey());
                LocalTime lastTime = LocalTime.parse(sortedSlots.lastKey());

                if (apptTime.isBefore(firstTime) || apptTime.isAfter(lastTime)) {
                    throw new IOException("Please choose a time within operating hours");
                }

                //Formats the input into the ormat of json keys, to prevent dummy entries/overwrites
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime parsedTime = LocalTime.parse(time, formatter);
                String standardizedTime = parsedTime.format(formatter);

                if (!slots.containsKey(standardizedTime)) {
                    throw new IOException("The time " + time + " is not a valid 30-minute slot for this doctor.");
                }

                if (slots.get(standardizedTime) == null) {
                    slots.put(time, patName);
                    found = true;
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
                } else {
                    throw new IOException("This slot is already booked. "
                                            + "Please edit the appointment if you wish to change it");
                }
                break;
            }
        }
        if (!found) {
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
            LocalTime formattedDate = LocalTime.parse(time);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * deletes an appointment according to the time and date from a doctor's schedule
     * @param appt
     */
    public static void delAppt(Appointment appt) throws IOException {
        String doctorName = appt.getDocName();
        String pat = appt.getPatName();
        String date = appt.getDate();
        String time = appt.getTime();
        boolean found = false;

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        Map<String, Object> data = mapper.readValue(file, Map.class);

        for (String name : data.keySet()) {
            if (name.equalsIgnoreCase(doctorName)) {

                Map<String, Object> doctorSchedule =
                        (Map<String, Object>) data.get(name);
                if (!doctorSchedule.containsKey(date)) {
                    throw new IOException("Date not found! Please choose a date within 7 days of today.");
                }

                Map<String, String> slots =
                        (Map<String, String>) doctorSchedule.get(date);
                TreeMap<String, Object> sortedSlots = new TreeMap<>(slots);

                LocalTime apptTime = LocalTime.parse(time);
                LocalTime firstTime = LocalTime.parse(sortedSlots.firstKey());
                LocalTime lastTime = LocalTime.parse(sortedSlots.lastKey());
                if (apptTime.isBefore(firstTime) || apptTime.isAfter(lastTime)) {
                    throw new IOException("Please choose a time within operating hours");
                }

                if (!slots.containsKey(time)) {
                    throw new IOException("There is no such time slot.");

                }

                if (slots.get(time) == null || !slots.get(time).equals(pat)) {
                    throw new IOException("No such appointment exists.");

                }
                slots.put(time, null);
                found = true;
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
                break;

            }
        }
        if (!found) {
            throw new IOException("Doctor not regiestered");
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);

    }

    /**
     * Reads the schedule.json file to find the patient currently booked at a specific slot.
     */
    public static String getPatientAtSlot(String doctorName, String date, String time) throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, Map<String, String>>> data =
                mapper.readValue(file, Map.class);

        String matchedDoctor = data.keySet().stream()
                .filter(name -> name.equalsIgnoreCase(doctorName))
                .findFirst()
                .orElse(null);

        if (matchedDoctor == null) {
            return null;
        }

        Map<String, Map<String, String>> doctorSchedule = data.get(matchedDoctor);
        if (!doctorSchedule.containsKey(date)) {
            return null;
        }

        return doctorSchedule.get(date).get(time);

    }
}
