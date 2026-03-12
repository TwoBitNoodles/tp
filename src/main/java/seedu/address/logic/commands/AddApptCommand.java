package seedu.address.logic.commands;

import seedu.address.model.Model;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

public class AddApptCommand extends Command {
    public static final String COMMAND_WORD = "add_appt";

    private String person;
    private String date;
    private String time;

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds an appointment for a person at the specified date and time.\n"
            + "Parameters: DATE (yyyy-mm-dd), TIME (H:MM)\n"
            + "Example: " + COMMAND_WORD + "(person) " + PREFIX_DATE + "2026-03-11 " + PREFIX_TIME + "9:00 ";

    public AddApptCommand(String person, String date, String time) {
        this.person = person;
        this.date = date;
        this.time = time;
    }
    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("Hello from AddAppointmentCommand! This feature is being implemented");
    }
}
