package seedu.address.ui;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * A panel containing the schedule of a doctor.
 */
public class SchedulePanel extends UiPart<Region> {

    private static final String FXML = "SchedulePanel.fxml";

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
    public void displaySchedule(Map<String, String> schedule) {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();

        // Only one column for slots
        ColumnConstraints timeCol = new ColumnConstraints();
        timeCol.setPrefWidth(80);
        ColumnConstraints slotCol = new ColumnConstraints();
        slotCol.setHgrow(Priority.ALWAYS);
        scheduleGrid.getColumnConstraints().addAll(timeCol, slotCol);

        int row = 0;
        for (Map.Entry<String, String> entry : schedule.entrySet()) {
            // Time label
            Label timeLabel = new Label(entry.getKey());
            timeLabel.getStyleClass().add("time-label");
            scheduleGrid.add(timeLabel, 0, row);

            // Slot
            Region slot = new Region();
            slot.setPrefSize(80, 30);
            if (entry.getValue() == null) {
                slot.getStyleClass().add("slot-available");
            } else {
                slot.getStyleClass().add("slot-booked");
            }
            scheduleGrid.add(slot, 1, row);
            row++;
        }
    }

    /**
     * Displays weekly schedule
     * @param weeklySchedule
     */
    public void displayWeeklySchedule(Map<String, Map<String, String>> weeklySchedule) {
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
                Region slot = new Region();
                slot.setPrefSize(80, 30);
                String patient = day.get(times[row]);
                if (patient == null) {
                    slot.getStyleClass().add("slot-available");
                } else {
                    slot.getStyleClass().add("slot-booked");
                }
                scheduleGrid.add(slot, colIndex++, row + 1);
            }
        }
    }
}

