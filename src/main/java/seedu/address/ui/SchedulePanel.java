package seedu.address.ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import seedu.address.storage.AppointmentManager;

/**
 * A panel containing the schedule of a doctor.
 */
public class SchedulePanel extends UiPart<Region> {

    private static final String FXML = "SchedulePanel.fxml";
    private static final int MAX_PATIENT_NAME_LENGTH = 24;
    private static final double SLOT_MIN_WIDTH = 180;
    private static final double SLOT_MIN_HEIGHT = 48;

    @FXML
    private GridPane scheduleGrid;

    public SchedulePanel() {
        super(FXML);
    }

    /**
     * Initializes the schedule panel by setting up the grid layout and styling.
     */
    @FXML
    public void initialize() {
        // allow grid to resize
        scheduleGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Displays the given schedule on the panel.
     *
     * @param schedule A map of time slots to patient names (or null if available).
     */
    public void displaySchedule(Map<String, String> schedule, String doctorName, int doctorId, LocalDate date) {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();

        // Only one column for slots
        ColumnConstraints timeCol = new ColumnConstraints();
        timeCol.setPrefWidth(80);
        ColumnConstraints slotCol = new ColumnConstraints();
        slotCol.setHgrow(Priority.ALWAYS);
        slotCol.setPrefWidth(SLOT_MIN_WIDTH);
        scheduleGrid.getColumnConstraints().addAll(timeCol, slotCol);

        int row = 0;
        for (Map.Entry<String, String> entry : schedule.entrySet()) {
            // Time label
            Label timeLabel = new Label(entry.getKey());
            timeLabel.getStyleClass().add("time-label");
            scheduleGrid.add(timeLabel, 0, row);

            Label slot = createSlotLabel(formatSlotText(entry.getValue(), doctorId, date, entry.getKey()));
            scheduleGrid.add(slot, 1, row);
            row++;
        }
    }

    /**
     * Displays weekly schedule
     * @param weeklySchedule
     */
    public void displayWeeklySchedule(Map<String, Map<String, String>> weeklySchedule, String doctorName,
                                      int doctorId) {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();

        if (weeklySchedule.isEmpty()) {
            return;
        }

        // Column 0: Time labels
        ColumnConstraints timeCol = new ColumnConstraints();
        timeCol.setPrefWidth(80);
        scheduleGrid.getColumnConstraints().add(timeCol);

        // Add columns for each day
        for (int i = 0; i < weeklySchedule.size(); i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setPrefWidth(SLOT_MIN_WIDTH);
            scheduleGrid.getColumnConstraints().add(col);
        }

        // Get all time slots from the first day
        String[] times = weeklySchedule.values().iterator().next().keySet().toArray(new String[0]);

        // Row 0: Date headers
        int colIndex = 1;
        for (String date : weeklySchedule.keySet()) {
            Label dateLabel = new Label(date);
            dateLabel.getStyleClass().add("date-label");
            scheduleGrid.add(dateLabel, colIndex++, 0);
        }

        // Rows: Time slots
        for (int row = 0; row < times.length; row++) {
            // Time label
            Label timeLabel = new Label(times[row]);
            timeLabel.getStyleClass().add("time-label");
            scheduleGrid.add(timeLabel, 0, row + 1);

            // Slot blocks
            colIndex = 1;
            for (String date : weeklySchedule.keySet()) {
                Map<String, String> day = weeklySchedule.get(date);
                Label slot = createSlotLabel(formatSlotText(day.get(times[row]), doctorId, LocalDate.parse(date),
                        times[row]));
                scheduleGrid.add(slot, colIndex++, row + 1);
            }
        }
    }

    private Label createSlotLabel(String slotText) {
        Label slot = new Label(slotText == null ? "" : slotText);
        slot.getStyleClass().add("schedule-slot");
        slot.setAlignment(Pos.CENTER);
        slot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        slot.setPrefWidth(SLOT_MIN_WIDTH);
        slot.setPrefHeight(SLOT_MIN_HEIGHT);
        slot.setWrapText(true);

        if (slotText == null || slotText.isBlank()) {
            slot.getStyleClass().add("slot-available");
        } else {
            slot.getStyleClass().add("slot-booked");
        }

        return slot;
    }

    private String formatSlotText(String patientName, int doctorId, LocalDate date, String time) {
        if (patientName == null) {
            return "";
        }

        String displayName = abbreviate(patientName);
        Integer patientId = findPatientId(doctorId, date, time);
        Integer apptId = findAppointmentId(doctorId, date, time);
        if (patientId == null && apptId == null) {
            return displayName;
        }

        StringBuilder text = new StringBuilder(displayName).append("\n(");
        if (patientId != null) {
            text.append("Patient ID: ").append(patientId);
        }
        if (patientId != null && apptId != null) {
            text.append(", ");
        }
        if (apptId != null) {
            text.append("Appt ID: ").append(apptId);
        }
        text.append(")");
        return text.toString();
    }

    private Integer findAppointmentId(int doctorId, LocalDate date, String time) {
        try {
            return AppointmentManager.findAppointmentIdBySlot(doctorId, date.toString(), time);
        } catch (IOException e) {
            return null;
        }
    }

    private Integer findPatientId(int doctorId, LocalDate date, String time) {
        try {
            return AppointmentManager.findPatientIdBySlot(doctorId, date.toString(), time);
        } catch (IOException e) {
            return null;
        }
    }

    private String abbreviate(String text) {
        if (text.length() <= MAX_PATIENT_NAME_LENGTH) {
            return text;
        }

        return text.substring(0, MAX_PATIENT_NAME_LENGTH - 3) + "...";
    }
}
