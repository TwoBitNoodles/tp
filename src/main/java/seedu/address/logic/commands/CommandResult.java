package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    private final Map<String, Map<String, String>> weeklySchedule;
    private final boolean isWeekly;
    private final Map<String, String> schedule;


    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit, Map<String, String> schedule,
                        Map<String, Map<String, String>> weeklySchedule, boolean isWeekly) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.schedule = schedule;
        this.weeklySchedule = weeklySchedule;
        this.isWeekly = isWeekly;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, null, null, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser} and {@code schedule},
     * and other fields set to their default value.
     * @param feedbackToUser
     * @param schedule
     */
    public CommandResult(String feedbackToUser, Map<String, String> schedule) {
        this(feedbackToUser, false, false, schedule, null, false);
    }

    /**
     * Weekly schedule constructor
     */
    public CommandResult(String feedbackToUser, Map<String, Map<String, String>> weeklySchedule, boolean isWeekly) {
        this(feedbackToUser, false, false, null, weeklySchedule, isWeekly);
    }

    /**
     * For help/exit commands
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, null, false);
    }

    public boolean isWeekly() {
        return isWeekly;
    }

    public Map<String, String> getSchedule() {
        return schedule;
    }

    public Map<String, Map<String, String>> getWeeklySchedule() {
        return weeklySchedule;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .toString();
    }

}
